package mx.edu.utez.uxvibe.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import mx.edu.utez.uxvibe.bean.ParticipantReportBean;
import mx.edu.utez.uxvibe.dao.ParticipantDao;
import mx.edu.utez.uxvibe.dao.RecordingDao;
import mx.edu.utez.uxvibe.model.ParticipantItem;

public class ParticipantStore implements ParticipantDao, RecordingDao {

  private static final ParticipantStore INSTANCE = new ParticipantStore();
  private final ParticipantDao participantDao = new ParticipantDao() {};
  private final RecordingDao recordingDao = new RecordingDao() {};
  private final Map<String, List<ParticipantItem>> participantsByUserAndTest =
    new LinkedHashMap<>();
  private final Map<String, ParticipantReportBean> reportsByParticipant =
    new LinkedHashMap<>();
  private final Map<String, String> participantNameByUserAndTest =
    new LinkedHashMap<>();

  private static final Logger LOGGER = Logger.getLogger(ParticipantStore.class.getName());

  private ParticipantStore() {
    try {
      recordingDao.ensureTableExists();
    } catch (Throwable ignored) {}
  }

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
    ParticipantItem participant = participantDao.registerCompletion(
      email,
      testName,
      startedAt,
      participantName
    );
    if (participant == null) {
      return null;
    }

    LOGGER.info("registerCompletion: created participant name='" + participant.getName() + "' for user=" + email + ", test=" + testName);

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

    List<ParticipantItem> participants = participantDao.listByUserAndTest(email, testName);
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
    LOGGER.info("saveSurveyResponse: email=" + email + ", test=" + testName + ", participant=" + participantName + ", question=" + question + ", answer=" + String.valueOf(answer));
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

    Integer numeric = null;
    try {
      List<Map<String, Object>> responses = report.getSurveyResponses();
      if (!responses.isEmpty()) {
        Map<String, Object> last = responses.get(responses.size() - 1);
        Object numObj = last.get("numeric");
        if (numObj != null) {
          numeric = Integer.parseInt(String.valueOf(numObj));
        }
      }
    } catch (Exception ignore) {}

    try {
      participantDao.saveSurveyResponseToDb(email, testName, rememberedParticipantName, question, answer, numeric);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public synchronized boolean deleteByTest(String email, String testName) {
    boolean ok = participantDao.deleteByTest(email, testName);
    recordingDao.deleteByTest(email, testName);

    String key = normalize(email) + "|" + normalize(testName);
    participantsByUserAndTest.remove(key);
    participantNameByUserAndTest.remove(key);
    List<String> toRemove = new ArrayList<>();
    for (String k : new ArrayList<>(reportsByParticipant.keySet())) {
      if (k.startsWith(key + "|")) {
        toRemove.add(k);
      }
    }
    for (String k : toRemove) {
      reportsByParticipant.remove(k);
    }
    return ok;
  }

  public synchronized boolean deleteParticipant(String email, String testName, String participantName) {
    boolean ok = participantDao.deleteParticipant(email, testName, participantName);
    recordingDao.deleteByParticipant(email, testName, participantName);

    String key = normalize(email) + "|" + normalize(testName);
    List<ParticipantItem> list = participantsByUserAndTest.get(key);
    if (list != null) {
      list.removeIf(p -> participantName.equals(p.getName()));
    }
    reportsByParticipant.remove(normalize(email) + "|" + normalize(testName) + "|" + normalize(participantName));
    return ok;
  }

  public synchronized void saveAudioAsset(
    String email,
    String testName,
    String participantName,
    String fileName,
    String audioUrl
  ) {
    LOGGER.info("saveAudioAsset: email=" + email + ", test=" + testName + ", participant=" + participantName + ", fileName=" + fileName);
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

    try {
      recordingDao.saveRecording(
        email,
        testName,
        rememberedParticipantName,
        "TEST_SESSION",
        fileName,
        audioUrl,
        report.getDurationMinutes() != null ? report.getDurationMinutes() * 60 : null
      );
    } catch (Throwable t) {
      t.printStackTrace();
    }

    try {
      participantDao.saveSurveyResponseToDb(email, testName, rememberedParticipantName, "audio", audioUrl, null);
    } catch (Throwable ignore) {}
  }

  public synchronized ParticipantReportBean getReport(
    String email,
    String testName,
    String participantName
  ) {
    String key = normalize(email) + "|" + normalize(testName) + "|" + normalize(participantName);
    ParticipantReportBean report = reportsByParticipant.get(key);
    if (report != null) {
      if (report.getAudioUrl() == null || report.getAudioUrl().isEmpty()) {
        Map<String, Object> rec = recordingDao.getRecording(email, testName, participantName, "TEST_SESSION");
        if (rec != null && rec.get("audioData") != null) {
          report.setAudioUrl(String.valueOf(rec.get("audioData")));
          if (rec.get("fileName") != null) {
            report.setAudioFileName(String.valueOf(rec.get("fileName")));
          }
        }
      }
      return report;
    }

    List<Map<String, Object>> persisted = participantDao.listResponses(email, testName, participantName == null ? "" : participantName);
    if (persisted != null && !persisted.isEmpty()) {
      report = new ParticipantReportBean();
      report.setParticipantName(participantName == null || participantName.trim().isEmpty() ? "Participante" : participantName);
      report.setTestName(safeTestName(testName));
      report.setDescription("Reporte cargado desde la base de datos.");
      for (Map<String, Object> row : persisted) {
        String q = row.get("question") == null ? null : String.valueOf(row.get("question"));
        Object ans = row.get("answer");
        Object numericObj = row.get("numeric");
        if (q != null && ("audio".equalsIgnoreCase(q) || "audio_url".equalsIgnoreCase(q))) {
          report.setAudioUrl(ans == null ? null : String.valueOf(ans));
        } else {
          Map<String, Object> r = new LinkedHashMap<>();
          r.put("question", q);
          r.put("answer", ans);
          if (numericObj != null) {
            r.put("numeric", numericObj);
          } else {
            report.addSurveyResponse(q, ans);
            continue;
          }
          report.getSurveyResponses().add(r);
        }
      }

      Map<String, Object> rec = recordingDao.getRecording(email, testName, participantName, "TEST_SESSION");
      if (rec != null && rec.get("audioData") != null) {
        report.setAudioUrl(String.valueOf(rec.get("audioData")));
        if (rec.get("fileName") != null) {
          report.setAudioFileName(String.valueOf(rec.get("fileName")));
        }
      }

      reportsByParticipant.put(key, report);
      return report;
    }

    if (participantName == null || participantName.trim().isEmpty()) {
      String prefix = normalize(email) + "|" + normalize(testName) + "|";
      for (Map.Entry<String, ParticipantReportBean> entry : reportsByParticipant.entrySet()) {
        if (entry.getKey().startsWith(prefix)) {
          return entry.getValue();
        }
      }
    }

    return null;
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
          1
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
