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
  String INSERT_SQL =
   "INSERT INTO PARTICIPANTES (EMAIL_USUARIO, NOMBRE_PRUEBA, NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO) VALUES (?, ?, ?, ?, ?, ?)";
  String LIST_BY_USER_AND_TEST_SQL =
   "SELECT NOMBRE_PARTICIPANTE, DESCRIPCION, DURACION_MINUTOS, FECHA_COMPLETADO FROM PARTICIPANTES WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE_PRUEBA)=LOWER(?) ORDER BY FECHA_COMPLETADO, ID_PARTICIPANTE";
  Map<String, List<ParticipantItem>> IN_MEMORY_PARTICIPANTS = new LinkedHashMap<>();

  default ParticipantItem registerCompletion(
   String email,
   String testName,
   LocalDateTime startedAt
  ) {
   return registerCompletion(email, testName, startedAt, null);
  }

  default ParticipantItem registerCompletion(
   String email,
   String testName,
   LocalDateTime startedAt,
   String participantName
  ) {
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
       Duration.between(startedAt, completedOn).toMinutes()
     );
   }

   String resolvedParticipantName = resolveParticipantName(participantName);
   String description =
     "Participación completada para la prueba " + safeTestName(testName) + ".";
   ParticipantItem participant = new ParticipantItem(
     resolvedParticipantName,
     description,
     durationMinutes,
     completedOn
   );

   String cacheKey = normalizedEmail + "|" + normalizedTestName;
   IN_MEMORY_PARTICIPANTS.computeIfAbsent(cacheKey, key -> new ArrayList<>()).add(participant);

   try (
     Connection conn = ConexionBD.getInstancia().getConnection();
     PreparedStatement ps = conn.prepareStatement(INSERT_SQL)
   ) {
     ps.setString(1, normalizedEmail);
     ps.setString(2, safeTestName(testName));
     ps.setString(3, resolvedParticipantName);
     ps.setString(4, description);
     ps.setInt(5, durationMinutes);
     ps.setTimestamp(6, Timestamp.valueOf(completedOn));
     ps.executeUpdate();
   } catch (SQLException e) {
     e.printStackTrace();
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
     PreparedStatement ps = conn.prepareStatement(LIST_BY_USER_AND_TEST_SQL)
   ) {
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

  private static String resolveParticipantName(String participantName) {
   if (participantName == null) {
     return "Participante 1";
   }
   String trimmed = participantName.trim();
   return trimmed.isEmpty() ? "Participante 1" : trimmed;
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
