package mx.edu.utez.uxvibe.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.edu.utez.uxvibe.bean.ParticipantReportBean;
import mx.edu.utez.uxvibe.dao.ParticipantDao;
import mx.edu.utez.uxvibe.model.ParticipantItem;

public class ParticipantStore implements ParticipantDao {

  private static final ParticipantStore INSTANCE = new ParticipantStore();
  private final ParticipantDao dao = new ParticipantDao() {};
  private final Map<String, List<ParticipantItem>> participantsByUserAndTest =
    new LinkedHashMap<>();
  private final Map<String, ParticipantReportBean> reportsByParticipant =
    new LinkedHashMap<>();
  private final Map<String, String> participantNameByUserAndTest =
    new LinkedHashMap<>();

  private ParticipantStore() {}

  public static ParticipantStore getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized ParticipantItem registerCompletion(
    String email,
    String testName,
    LocalDateTime startedAt,
    String participantName
  ) {
    ParticipantItem participant = dao.registerCompletion(
      email,
      testName,
      startedAt,
      participantName
    );
    if (participant == null) {
      return null;
    }

    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return participant;
    }

    String participantKey = normalizedEmail + "|" + normalizedTestName;
    List<ParticipantItem> participants =
      participantsByUserAndTest.computeIfAbsent(participantKey, key -> new ArrayList<>());
    participants.add(participant);
    participantNameByUserAndTest.put(participantKey, participant.getName());

    ParticipantReportBean report = ensureReport(
      normalizedEmail,
      normalizedTestName,
      participant.getName()
    );
    report.setParticipantName(participant.getName());
    report.setTestName(safeTestName(testName));
    report.setDescription(participant.getDescription());
    report.setDurationMinutes(participant.getDurationMinutes());
    report.setCompletedOn(participant.getCompletedOn());
    return participant;
  }

  @Override
  public synchronized List<ParticipantItem> listByUserAndTest(
    String email,
    String testName
  ) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return new ArrayList<>();
    }

    List<ParticipantItem> participants = dao.listByUserAndTest(email, testName);
    if (!participants.isEmpty()) {
      participantsByUserAndTest.put(
        normalizedEmail + "|" + normalizedTestName,
        new ArrayList<>(participants)
      );
      return new ArrayList<>(participants);
    }

    List<ParticipantItem> cachedParticipants = participantsByUserAndTest.get(
      normalizedEmail + "|" + normalizedTestName
    );
    if (cachedParticipants == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(cachedParticipants);
  }

  public synchronized void saveSurveyResponse(
    String email,
    String testName,
    String participantName,
    String question,
    Object answer
  ) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    String rememberedParticipantName = resolveParticipantName(
      participantName,
      1
    );
    participantNameByUserAndTest.put(
      normalizedEmail + "|" + normalizedTestName,
      rememberedParticipantName
    );
    ParticipantReportBean report = ensureReport(
      normalizedEmail,
      normalizedTestName,
      rememberedParticipantName
    );
    report.addSurveyResponse(question, answer);
  }

  public synchronized void saveAudioAsset(
    String email,
    String testName,
    String participantName,
    String fileName,
    String audioUrl
  ) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    String rememberedParticipantName = resolveParticipantName(
      participantName,
      1
    );
    participantNameByUserAndTest.put(
      normalizedEmail + "|" + normalizedTestName,
      rememberedParticipantName
    );
    ParticipantReportBean report = ensureReport(
      normalizedEmail,
      normalizedTestName,
      rememberedParticipantName
    );
    report.setAudioFileName(fileName);
    report.setAudioUrl(audioUrl);
  }

  public synchronized ParticipantReportBean getReport(
    String email,
    String testName,
    String participantName
  ) {
    return reportsByParticipant.get(
      normalize(email) +
        "|" +
        normalize(testName) +
        "|" +
        normalize(participantName)
    );
  }

  private ParticipantReportBean ensureReport(
    String normalizedEmail,
    String normalizedTestName,
    String participantName
  ) {
    String normalizedParticipantName = normalize(participantName);
    String cacheKey =
      normalizedEmail +
      "|" +
      normalizedTestName +
      "|" +
      normalizedParticipantName;
    ParticipantReportBean report = reportsByParticipant.get(cacheKey);
    if (report == null) {
      report = new ParticipantReportBean();
      report.setParticipantName(
        resolveParticipantName(
          participantName,
          participantNameByUserAndTest.containsKey(
            normalizedEmail + "|" + normalizedTestName
          )
            ? 1
            : 1
        )
      );
      report.setTestName("Prueba sin nombre");
      report.setDescription("Participación iniciada para la prueba.");
      reportsByParticipant.put(cacheKey, report);
    }
    return report;
  }

  private String resolveParticipantName(
    String participantName,
    int fallbackIndex
  ) {
    String trimmed = participantName == null ? "" : participantName.trim();
    if (!trimmed.isEmpty()) {
      return trimmed;
    }
    return "Participante " + fallbackIndex;
  }

  private String safeTestName(String testName) {
    if (testName == null) {
      return "Prueba sin nombre";
    }
    String trimmed = testName.trim();
    return trimmed.isEmpty() ? "Prueba sin nombre" : trimmed;
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toLowerCase();
  }
}
