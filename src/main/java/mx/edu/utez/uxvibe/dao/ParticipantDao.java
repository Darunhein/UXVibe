package mx.edu.utez.uxvibe.dao;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mx.edu.utez.uxvibe.ConexionBD;
import mx.edu.utez.uxvibe.model.ParticipantItem;
import mx.edu.utez.uxvibe.util.QuestionNumbers;

public interface ParticipantDao {
  String INSERT_SQL = "INSERT INTO PARTICIPANTES (EMAIL_USUARIO, NOMBRE_PRUEBA, NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO) VALUES (?, ?, ?, ?, NUMTODSINTERVAL(?, 'MINUTE'), ?)";
  String FIND_PARTICIPANT_ID_SQL = "SELECT ID_PARTICIPANTE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?) ORDER BY ID_PARTICIPANTE DESC";
  String UPDATE_COMPLETION_SQL = "UPDATE PARTICIPANTES SET DESCRIPCION = ?, DURACION_MINUTOS = NUMTODSINTERVAL(?, 'MINUTE'), FECHA_COMPLETADO = ? WHERE ID_PARTICIPANTE = ?";
  String MERGE_RESPONSE_SQL = "MERGE INTO RESPUESTAS dest "
      + "USING (SELECT ? AS ID_PRUEBA, ? AS ID_PARTICIPANTE, ? AS NUMERO_PREGUNTA, ? AS RESPUESTA FROM dual) src "
      + "ON (dest.ID_PARTICIPANTE = src.ID_PARTICIPANTE AND dest.NUMERO_PREGUNTA = src.NUMERO_PREGUNTA) "
      + "WHEN MATCHED THEN UPDATE SET dest.RESPUESTA = src.RESPUESTA, dest.ID_PRUEBA = src.ID_PRUEBA "
      + "WHEN NOT MATCHED THEN INSERT (ID_PRUEBA, ID_PARTICIPANTE, NUMERO_PREGUNTA, RESPUESTA) "
      + "VALUES (src.ID_PRUEBA, src.ID_PARTICIPANTE, src.NUMERO_PREGUNTA, src.RESPUESTA)";
  String FIND_RESPONSE_ID_SQL = "SELECT ID_RESPUESTA FROM RESPUESTAS WHERE ID_PARTICIPANTE = ? AND NUMERO_PREGUNTA = ?";
  String INSERT_AUDIO_SQL = "INSERT INTO RESPUESTAS (ID_PRUEBA, ID_PARTICIPANTE, NUMERO_PREGUNTA, RESPUESTA, AUDIO) VALUES (?, ?, ?, ?, ?)";
  String UPDATE_AUDIO_SQL = "UPDATE RESPUESTAS SET ID_PRUEBA = ?, RESPUESTA = ?, AUDIO = ? WHERE ID_RESPUESTA = ?";
  String DELETE_PARTICIPANT_RESPONSES_SQL = "DELETE FROM RESPUESTAS WHERE ID_PARTICIPANTE IN ("
      + "SELECT ID_PARTICIPANTE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?))";
  String DELETE_PARTICIPANT_SQL = "DELETE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?)";
  String DELETE_RESPONSES_BY_TEST_SQL = "DELETE FROM RESPUESTAS WHERE ID_PARTICIPANTE IN ("
      + "SELECT ID_PARTICIPANTE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?))";
  String DELETE_PARTICIPANTES_BY_TEST_SQL = "DELETE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?)";
  String UPDATE_PARTICIPANT_NAME_SQL = "UPDATE PARTICIPANTES SET NOMBRE_PARTICIPANTE = ? WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?)";
  String LIST_BY_USER_AND_TEST_SQL = "SELECT NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) ORDER BY FECHA_COMPLETADO, ID_PARTICIPANTE";
  String LIST_RESPONSES_SQL = "SELECT r.NUMERO_PREGUNTA, r.RESPUESTA, r.AUDIO FROM RESPUESTAS r "
      + "JOIN PARTICIPANTES p ON p.ID_PARTICIPANTE = r.ID_PARTICIPANTE "
      + "WHERE LOWER(p.EMAIL_USUARIO)=LOWER(?) AND LOWER(p.NOMBRE_PRUEBA)=LOWER(?) AND LOWER(p.NOMBRE_PARTICIPANTE)=LOWER(?) "
      + "ORDER BY r.NUMERO_PREGUNTA";
  Map<String, List<ParticipantItem>> IN_MEMORY_PARTICIPANTS = new LinkedHashMap<>();

  java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ParticipantDao.class.getName());

  default ParticipantItem registerCompletion(
      String email,
      String testName,
      LocalDateTime startedAt) {
    return registerCompletion(email, testName, startedAt, null);
  }

  default ParticipantItem registerCompletion(
      String email,
      String testName,
      LocalDateTime startedAt,
      String participantName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return null;
    }

    LocalDateTime completedOn = LocalDateTime.now(ZoneId.of("America/Mexico_City"));
    int durationMinutes = 5;
    if (startedAt != null) {
      durationMinutes = (int) Math.max(
          1,
          Duration.between(startedAt, completedOn).toMinutes());
    }

    String cacheKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> existingList = IN_MEMORY_PARTICIPANTS.computeIfAbsent(cacheKey, key -> new ArrayList<>());
    String resolvedParticipantName = resolveParticipantName(participantName, existingList.size());
    String description = "Participación completada para la prueba " + safeTestName(testName) + ".";
    ParticipantItem participant = new ParticipantItem(
        resolvedParticipantName,
        description,
        durationMinutes,
        completedOn);

    long participantId = ensureParticipantId(
        email,
        testName,
        resolvedParticipantName,
        description,
        durationMinutes);
    if (participantId > 0) {
      updateCompletion(participantId, description, durationMinutes, completedOn);
    }

    boolean alreadyListed = existingList.stream()
        .anyMatch(item -> item.getName() != null && item.getName().equalsIgnoreCase(resolvedParticipantName));
    if (alreadyListed) {
      for (ParticipantItem item : existingList) {
        if (item.getName() != null && item.getName().equalsIgnoreCase(resolvedParticipantName)) {
          item.setDescription(description);
          item.setDurationMinutes(durationMinutes);
          item.setCompletedOn(completedOn);
        }
      }
    } else {
      existingList.add(participant);
    }

    return participant;
  }

  default List<ParticipantItem> listByUserAndTest(String email, String testName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return new ArrayList<>();
    }

    String cacheKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> participants = queryParticipants(LIST_BY_USER_AND_TEST_SQL, email, testName);
    if (participants != null) {
      IN_MEMORY_PARTICIPANTS.put(cacheKey, new ArrayList<>(participants));
      return participants;
    }

    List<ParticipantItem> cachedParticipants = IN_MEMORY_PARTICIPANTS.get(cacheKey);
    if (cachedParticipants == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(cachedParticipants);
  }

  default void saveSurveyResponseToDb(String email, String testName, String participantName, String question,
      Object answer, Integer numeric) {
    int questionNumber = QuestionNumbers.toNumber(question);
    if (questionNumber < 0 || QuestionNumbers.isAudio(questionNumber)) {
      return;
    }
    long participantId = ensureParticipantId(email, testName, participantName);
    if (participantId <= 0) {
      LOGGER.warning("Cannot save response: missing ID_PARTICIPANTE for " + participantName);
      return;
    }
    long testId = resolveTestId(email, testName);
    if (testId <= 0) {
      LOGGER.warning("Refuse survey save: no PRUEBAS row for email=" + normalizeEmail(email)
          + ", test=" + safeTestName(testName));
      return;
    }
    String storedAnswer = answer == null ? (numeric == null ? null : String.valueOf(numeric))
        : String.valueOf(answer);
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(MERGE_RESPONSE_SQL)) {
      ps.setLong(1, testId);
      ps.setLong(2, participantId);
      ps.setInt(3, questionNumber);
      ps.setString(4, storedAnswer);
      int affected = ps.executeUpdate();
      if (affected > 0) {
        LOGGER.info(
            "Saved response: testId=" + testId + ", participantId=" + participantId
                + ", question=" + question + " (" + questionNumber + "), answer=" + storedAnswer);
      } else {
        LOGGER.warning("No rows affected when saving response for participantId=" + participantId
            + ", question=" + question);
      }
    } catch (SQLException ex) {
      if (!ConexionBD.isUnavailable(ex)) {
        LOGGER.log(java.util.logging.Level.SEVERE, "Error saving response to DB", ex);
      }
    }
  }

  default boolean saveAudioToDb(String email, String testName, String participantName, String fileName,
      String audioData) {
    return saveAudioToDb(email, testName, participantName, fileName, audioData, QuestionNumbers.AUDIO);
  }

  default boolean saveAudioToDb(String email, String testName, String participantName, String fileName,
      String audioData, int questionNumber) {
    if (!QuestionNumbers.isAudio(questionNumber)) {
      questionNumber = QuestionNumbers.AUDIO;
    }
    long participantId = ensureParticipantId(email, testName, participantName);
    if (participantId <= 0) {
      LOGGER.warning("Cannot save audio: missing ID_PARTICIPANTE for " + participantName);
      return false;
    }
    long testId = resolveTestId(email, testName);
    if (testId <= 0) {
      LOGGER.warning("Refuse audio save: no PRUEBAS row for email=" + normalizeEmail(email)
          + ", test=" + safeTestName(testName));
      return false;
    }
    String fallbackName = questionNumber == QuestionNumbers.AUDIO_MIC
        ? "prueba-microfono.webm"
        : "grabacion-sesion.webm";
    String storedName = fileName == null || fileName.trim().isEmpty() ? fallbackName : fileName.trim();
    try (Connection conn = ConexionBD.getInstancia().getConnection()) {
      Long responseId = findResponseId(conn, participantId, questionNumber);
      if (responseId != null) {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_AUDIO_SQL)) {
          ps.setLong(1, testId);
          ps.setString(2, storedName);
          setAudioAsBlob(ps, 3, audioData);
          ps.setLong(4, responseId);
          return ps.executeUpdate() > 0;
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(INSERT_AUDIO_SQL)) {
        ps.setLong(1, testId);
        ps.setLong(2, participantId);
        ps.setInt(3, questionNumber);
        ps.setString(4, storedName);
        setAudioAsBlob(ps, 5, audioData);
        return ps.executeUpdate() > 0;
      }
    } catch (SQLException ex) {
      if (!ConexionBD.isUnavailable(ex)) {
        LOGGER.log(java.util.logging.Level.SEVERE, "Error saving audio to RESPUESTAS", ex);
      }
      return false;
    }
  }

  default Map<String, Object> getAudioFromDb(String email, String testName, String participantName) {
    return getAudioFromDb(email, testName, participantName, QuestionNumbers.AUDIO);
  }

  default Map<String, Object> getAudioFromDb(String email, String testName, String participantName,
      int questionNumber) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(LIST_RESPONSES_SQL)) {
      ps.setString(1, normalizeEmail(email));
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName == null ? "" : participantName);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (rs.getInt("NUMERO_PREGUNTA") != questionNumber) {
            continue;
          }
          Map<String, Object> result = new LinkedHashMap<>();
          result.put("fileName", rs.getString("RESPUESTA"));
          result.put("audioData", readAudioColumn(rs, "AUDIO"));
          return result;
        }
      }
    } catch (SQLException ex) {
      if (!ConexionBD.isUnavailable(ex)) {
        LOGGER.log(java.util.logging.Level.WARNING, "Error reading audio from RESPUESTAS", ex);
      }
    }
    return null;
  }

  default java.util.List<java.util.Map<String, Object>> listResponses(String email, String testName,
      String participantName) {
    java.util.List<java.util.Map<String, Object>> out = new java.util.ArrayList<>();
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(LIST_RESPONSES_SQL)) {
      ps.setString(1, normalizeEmail(email));
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName == null ? "" : participantName);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int number = rs.getInt("NUMERO_PREGUNTA");
          java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
          row.put("questionNumber", number);
          row.put("question", QuestionNumbers.toName(number));
          row.put("answer", rs.getString("RESPUESTA"));
          String audio = readAudioColumn(rs, "AUDIO");
          if (audio != null && !audio.isEmpty()) {
            row.put("audio", audio);
          }
          String answer = rs.getString("RESPUESTA");
          Integer numeric = parseNumeric(answer);
          row.put("numeric", numeric);
          out.add(row);
        }
      }
    } catch (SQLException ex) {
      if (!ConexionBD.isUnavailable(ex)) {
        LOGGER.log(java.util.logging.Level.SEVERE, "Error listing responses from DB", ex);
      }
    }
    LOGGER.info("Loaded " + out.size() + " responses for user=" + normalizeEmail(email) + ", test="
        + safeTestName(testName) + ", participant=" + (participantName == null ? "" : participantName));
    return out;
  }

  default boolean deleteParticipant(String email, String testName, String participantName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty() || participantName == null) {
      return false;
    }
    int affected = 0;
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_PARTICIPANT_RESPONSES_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName);
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps2 = conn.prepareStatement(DELETE_PARTICIPANT_SQL)) {
      ps2.setString(1, normalizedEmail);
      ps2.setString(2, safeTestName(testName));
      ps2.setString(3, participantName);
      affected = ps2.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    boolean removedFromMemory = false;
    String cacheKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> list = IN_MEMORY_PARTICIPANTS.get(cacheKey);
    if (list != null) {
      removedFromMemory = list.removeIf(p -> p.getName() != null && p.getName().equalsIgnoreCase(participantName));
    }
    return affected > 0 || removedFromMemory;
  }

  default boolean renameParticipant(String email, String testName, String fromName, String toName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty() || fromName == null || toName == null
        || toName.trim().isEmpty() || fromName.equals(toName.trim())) {
      return false;
    }
    String trimmedTo = toName.trim();
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(UPDATE_PARTICIPANT_NAME_SQL)) {
      ps.setString(1, trimmedTo);
      ps.setString(2, normalizedEmail);
      ps.setString(3, safeTestName(testName));
      ps.setString(4, fromName);
      ps.executeUpdate();
    } catch (SQLException ex) {
      LOGGER.log(java.util.logging.Level.WARNING, "Could not rename participant row", ex);
    }
    String cacheKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> list = IN_MEMORY_PARTICIPANTS.get(cacheKey);
    if (list != null) {
      for (ParticipantItem item : list) {
        if (item.getName() != null && item.getName().equalsIgnoreCase(fromName)) {
          item.setName(trimmedTo);
        }
      }
    }
    return true;
  }

  default boolean deleteByTest(String email, String testName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return false;
    }
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_RESPONSES_BY_TEST_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, safeTestName(testName));
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps2 = conn.prepareStatement(DELETE_PARTICIPANTES_BY_TEST_SQL)) {
      ps2.setString(1, normalizedEmail);
      ps2.setString(2, safeTestName(testName));
      ps2.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    IN_MEMORY_PARTICIPANTS.remove(normalizedEmail + "|" + normalizedTestName);
    return true;
  }

  default long ensureParticipantId(String email, String testName, String participantName) {
    String description = "Participación en curso para la prueba " + safeTestName(testName) + ".";
    return ensureParticipantId(email, testName, participantName, description, 0);
  }

  default long ensureParticipantId(
      String email,
      String testName,
      String participantName,
      String description,
      int durationMinutes) {
    Long existing = findParticipantId(email, testName, participantName);
    if (existing != null && existing > 0) {
      return existing;
    }
    String normalizedEmail = normalizeEmail(email);
    String resolvedName = resolveParticipantName(participantName, 0);
    if (normalizedEmail.isEmpty() || safeTestName(testName).isEmpty()) {
      return -1;
    }
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Mexico_City"));
    insertParticipant(
        INSERT_SQL,
        normalizedEmail,
        testName,
        resolvedName,
        description,
        durationMinutes,
        now);
    Long created = findParticipantId(email, testName, resolvedName);
    return created == null ? -1 : created;
  }

  private static Long findParticipantId(String email, String testName, String participantName) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(FIND_PARTICIPANT_ID_SQL)) {
      ps.setString(1, normalizeEmail(email));
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName == null ? "" : participantName.trim());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          long id = rs.getLong("ID_PARTICIPANTE");
          return rs.wasNull() ? null : id;
        }
      }
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        LOGGER.log(java.util.logging.Level.WARNING, "Could not resolve ID_PARTICIPANTE", e);
      }
    }
    return null;
  }

  private static long resolveTestId(String email, String testName) {
    return new TestDao() {
    }.findIdByEmailAndName(email, testName);
  }

  private static boolean insertParticipant(
      String sql,
      String email,
      String testName,
      String participantName,
      String description,
      int durationMinutes,
      LocalDateTime completedOn) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName);
      ps.setString(4, description);
      ps.setInt(5, durationMinutes);
      ps.setTimestamp(6, Timestamp.valueOf(completedOn));
      int affected = ps.executeUpdate();
      if (affected > 0) {
        LOGGER.info("Inserted participant: email=" + email + ", test=" + safeTestName(testName)
            + ", participant=" + participantName);
        return true;
      }
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        LOGGER.log(java.util.logging.Level.WARNING, "Insert participant failed with SQL variant", e);
      }
    }
    return false;
  }

  private static void updateCompletion(
      long participantId,
      String description,
      int durationMinutes,
      LocalDateTime completedOn) {
    runCompletionUpdate(UPDATE_COMPLETION_SQL, participantId, description, durationMinutes, completedOn);
  }

  private static boolean runCompletionUpdate(
      String sql,
      long participantId,
      String description,
      int durationMinutes,
      LocalDateTime completedOn) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, description);
      ps.setInt(2, durationMinutes);
      ps.setTimestamp(3, Timestamp.valueOf(completedOn));
      ps.setLong(4, participantId);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        LOGGER.log(java.util.logging.Level.WARNING, "Update completion failed with SQL variant", e);
      }
      return false;
    }
  }

  private static List<ParticipantItem> queryParticipants(String sql, String email, String testName) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, normalizeEmail(email));
      ps.setString(2, safeTestName(testName));
      List<ParticipantItem> participants = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ParticipantItem participant = new ParticipantItem();
          participant.setName(rs.getString("NOMBRE_PARTICIPANTE"));
          participant.setDescription(rs.getString("DESCRIPCION"));
          participant.setDurationMinutes(readDurationMinutes(rs));
          Timestamp completedOn = rs.getTimestamp("FECHA_COMPLETADO");
          if (completedOn != null) {
            participant.setCompletedOn(completedOn.toLocalDateTime());
          }
          participants.add(participant);
        }
      }
      return participants;
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        LOGGER.log(java.util.logging.Level.WARNING, "List participants failed with SQL variant", e);
      }
      return null;
    }
  }

  private static Long findResponseId(Connection conn, long participantId, int questionNumber) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(FIND_RESPONSE_ID_SQL)) {
      ps.setLong(1, participantId);
      ps.setInt(2, questionNumber);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          long id = rs.getLong("ID_RESPUESTA");
          return rs.wasNull() ? null : id;
        }
      }
    }
    return null;
  }

  private static void setAudioAsBlob(PreparedStatement ps, int index, String audioData) throws SQLException {
    if (audioData == null) {
      ps.setNull(index, Types.BLOB);
      return;
    }
    try {
      ps.setBytes(index, Base64.getDecoder().decode(audioData));
    } catch (IllegalArgumentException notBase64) {
      ps.setBytes(index, audioData.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
  }

  private static String readAudioColumn(ResultSet rs, String column) throws SQLException {
    try {
      Blob blob = rs.getBlob(column);
      if (blob != null && blob.length() > 0) {
        byte[] bytes = blob.getBytes(1, (int) blob.length());
        return Base64.getEncoder().encodeToString(bytes);
      }
    } catch (SQLException ignored) {
    }
    try {
      String asString = rs.getString(column);
      if (asString != null && !asString.isEmpty()) {
        return asString;
      }
    } catch (SQLException ignored) {
    }
    try {
      Clob clob = rs.getClob(column);
      if (clob != null && clob.length() > 0) {
        return clob.getSubString(1, (int) Math.min(clob.length(), Integer.MAX_VALUE));
      }
    } catch (SQLException ignored) {
    }
    return null;
  }

  private static int readDurationMinutes(ResultSet rs) throws SQLException {
    try {
      Duration duration = rs.getObject("DURACION_MINUTOS", Duration.class);
      if (duration != null) {
        return (int) Math.max(0, duration.toMinutes());
      }
    } catch (Exception ignored) {
    }
    Object raw = rs.getObject("DURACION_MINUTOS");
    if (raw instanceof Duration) {
      return (int) Math.max(0, ((Duration) raw).toMinutes());
    }
    if (raw == null) {
      return 0;
    }
    String text = String.valueOf(raw).trim();
    try {
      int space = text.indexOf(' ');
      String timePart = space >= 0 ? text.substring(space + 1) : text;
      String[] bits = timePart.split(":");
      if (bits.length >= 2) {
        int hours = Integer.parseInt(bits[0].replace("+", "").replace("-", ""));
        int minutes = Integer.parseInt(bits[1]);
        return Math.max(0, hours * 60 + minutes);
      }
    } catch (NumberFormatException ignored) {
    }
    return 0;
  }

  private static Integer parseNumeric(String answer) {
    if (answer == null) {
      return null;
    }
    try {
      return Integer.parseInt(answer.trim());
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private static String resolveParticipantName(String participantName, int currentCount) {
    if (participantName == null) {
      return "Participante " + (currentCount + 1);
    }
    String trimmed = participantName.trim();
    return trimmed.isEmpty() ? "Participante " + (currentCount + 1) : trimmed;
  }

  private static String safeTestName(String testName) {
    if (testName == null) {
      return "Prueba sin nombre";
    }
    String trimmed = testName.trim();
    return trimmed.isEmpty() ? "Prueba sin nombre" : trimmed;
  }

  private static String normalizeText(String value) {
    return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
