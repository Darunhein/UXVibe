package mx.edu.utez.uxvibe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mx.edu.utez.uxvibe.ConexionBD;
import mx.edu.utez.uxvibe.model.ParticipantItem;

public interface ParticipantDao {
  String INSERT_SQL = "INSERT INTO PARTICIPANTES (EMAIL_USUARIO, NOMBRE_PRUEBA, NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO) VALUES (?, ?, ?, ?, ?, ?)";
  // note: some DB schemas don't have a FECHA column — omit it and let DB defaults
  // handle timestamps
  String INSERT_RESPONSE_SQL = "INSERT INTO RESPUESTAS (EMAIL_USUARIO, NOMBRE_PRUEBA, NOMBRE_PARTICIPANTE, PREGUNTA, RESPUESTA, NUMERICO) VALUES (?, ?, ?, ?, ?, ?)";
  String DELETE_PARTICIPANT_RESPONSES_SQL = "DELETE FROM RESPUESTAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?)";
  String DELETE_PARTICIPANT_SQL = "DELETE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?)";
  String DELETE_RESPONSES_BY_TEST_SQL = "DELETE FROM RESPUESTAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?)";
  String DELETE_PARTICIPANTES_BY_TEST_SQL = "DELETE FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?)";
  String LIST_BY_USER_AND_TEST_SQL = "SELECT NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) ORDER BY FECHA_COMPLETADO, ID_PARTICIPANTE";
  // omit FECHA in select because some schemas don't include that column
  String LIST_RESPONSES_SQL = "SELECT PREGUNTA, RESPUESTA, NUMERICO FROM RESPUESTAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) AND LOWER(NOMBRE_PARTICIPANTE)=LOWER(?) ORDER BY 1";
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

    existingList.add(participant);

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, safeTestName(testName));
      ps.setString(3, resolvedParticipantName);
      ps.setString(4, description);
      ps.setInt(5, durationMinutes);
      ps.setTimestamp(6, Timestamp.valueOf(completedOn));
      int affected = ps.executeUpdate();
      if (affected > 0) {
        LOGGER.info("Inserted participant: email=" + normalizedEmail + ", test=" + safeTestName(testName)
            + ", participant=" + resolvedParticipantName);
      } else {
        LOGGER.warning("No rows inserted for participant: email=" + normalizedEmail + ", test=" + safeTestName(testName)
            + ", participant=" + resolvedParticipantName);
      }
    } catch (SQLException e) {
      LOGGER.log(java.util.logging.Level.SEVERE, "Error inserting participant", e);
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

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(LIST_BY_USER_AND_TEST_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, safeTestName(testName));
      List<ParticipantItem> participants = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          ParticipantItem participant = new ParticipantItem();
          participant.setName(rs.getString("NOMBRE_PARTICIPANTE"));
          participant.setDescription(rs.getString("DESCRIPCION"));
          participant.setDurationMinutes(rs.getInt("DURACION_MINUTOS"));
          Timestamp completedOn = rs.getTimestamp("FECHA_COMPLETADO");
          if (completedOn != null) {
            participant.setCompletedOn(completedOn.toLocalDateTime());
          }
          participants.add(participant);
        }
      }
      if (!participants.isEmpty()) {
        IN_MEMORY_PARTICIPANTS.put(cacheKey, new ArrayList<>(participants));
        // log loaded participant names
        StringBuilder names = new StringBuilder();
        for (ParticipantItem p : participants) {
          names.append(p.getName()).append(",");
        }
        LOGGER.info("listByUserAndTest loaded " + participants.size() + " participants for user="
            + normalizeEmail(email) + ", test=" + safeTestName(testName) + ". names=" + names.toString());
        return participants;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    List<ParticipantItem> cachedParticipants = IN_MEMORY_PARTICIPANTS.get(cacheKey);
    if (cachedParticipants == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(cachedParticipants);
  }

  default void saveSurveyResponseToDb(String email, String testName, String participantName, String question,
      Object answer, Integer numeric) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_RESPONSE_SQL)) {
      ps.setString(1, normalizeEmail(email));
      ps.setString(2, safeTestName(testName));
      ps.setString(3, participantName == null ? "" : participantName);
      ps.setString(4, question);
      ps.setString(5, answer == null ? null : String.valueOf(answer));
      if (numeric != null) {
        ps.setInt(6, numeric);
      } else {
        ps.setNull(6, java.sql.Types.INTEGER);
      }
      int affected = ps.executeUpdate();
      if (affected > 0) {
        LOGGER.info(
            "Inserted response: email=" + normalizeEmail(email) + ", test=" + safeTestName(testName) + ", participant="
                + (participantName == null ? "" : participantName) + ", question=" + question + ", numeric=" + numeric);
      } else {
        LOGGER.warning("No rows affected when inserting response for email=" + normalizeEmail(email) + ", test="
            + safeTestName(testName) + ", participant=" + participantName + ", question=" + question);
      }
    } catch (SQLException ex) {
      LOGGER.log(java.util.logging.Level.SEVERE, "Error saving response to DB", ex);
    }
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
          java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
          row.put("question", rs.getString("PREGUNTA"));
          row.put("answer", rs.getString("RESPUESTA"));
          int num = rs.getInt("NUMERICO");
          if (rs.wasNull()) {
            row.put("numeric", null);
          } else {
            row.put("numeric", num);
          }
          out.add(row);
        }
      }
    } catch (SQLException ex) {
      LOGGER.log(java.util.logging.Level.SEVERE, "Error listing responses from DB", ex);
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
    // delete responses
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_PARTICIPANT_RESPONSES_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, normalizedTestName);
      ps.setString(3, participantName);
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    // delete participant row
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps2 = conn.prepareStatement(DELETE_PARTICIPANT_SQL)) {
      ps2.setString(1, normalizedEmail);
      ps2.setString(2, normalizedTestName);
      ps2.setString(3, participantName);
      ps2.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    // remove from in-memory participants cache
    String cacheKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> list = IN_MEMORY_PARTICIPANTS.get(cacheKey);
    if (list != null) {
      list.removeIf(p -> participantName.equals(p.getName()));
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
      ps.setString(2, normalizedTestName);
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps2 = conn.prepareStatement(DELETE_PARTICIPANTES_BY_TEST_SQL)) {
      ps2.setString(1, normalizedEmail);
      ps2.setString(2, normalizedTestName);
      ps2.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    // remove in-memory cache
    IN_MEMORY_PARTICIPANTS.remove(normalizedEmail + "|" + normalizedTestName);
    return true;
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
