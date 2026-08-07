package mx.edu.utez.uxvibe.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.edu.utez.uxvibe.model.ParticipantItem;

public class ParticipantStore {

  private static final ParticipantStore INSTANCE = new ParticipantStore();
  private final Map<String, List<ParticipantItem>> participantsByUserAndTest =
    new LinkedHashMap<>();

  private ParticipantStore() {}

  public static ParticipantStore getInstance() {
    return INSTANCE;
  }

  public synchronized ParticipantItem registerCompletion(
    String email,
    String testName,
    LocalDateTime startedAt
  ) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return null;
    }

    List<ParticipantItem> participants =
      participantsByUserAndTest.computeIfAbsent(
        normalizedEmail + "|" + normalizedTestName,
        key -> new ArrayList<>()
      );

    LocalDateTime completedOn = LocalDateTime.now(ZoneId.systemDefault());
    int durationMinutes = 5;
    if (startedAt != null) {
      ZonedDateTime startedAtZoned = startedAt.atZone(ZoneId.systemDefault());
      ZonedDateTime completedAtZoned = completedOn.atZone(
        ZoneId.systemDefault()
      );
      durationMinutes = (int) Math.max(
        1,
        Duration.between(startedAtZoned, completedAtZoned).toMinutes()
      );
    }

    String safeTestName = testName == null ? "" : testName.trim();
    ParticipantItem participant = new ParticipantItem(
      "Participante " + (participants.size() + 1),
      "Participación completada para la prueba " + safeTestName + ".",
      durationMinutes,
      completedOn
    );
    participants.add(participant);
    return participant;
  }

  public synchronized List<ParticipantItem> listByUserAndTest(
    String email,
    String testName
  ) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return new ArrayList<>();
    }

    List<ParticipantItem> participants = participantsByUserAndTest.get(
      normalizedEmail + "|" + normalizedTestName
    );
    if (participants == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(participants);
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase();
  }
}
