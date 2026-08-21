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
import mx.edu.utez.uxvibe.util.QuestionNumbers;

public class ParticipantStore implements ParticipantDao, RecordingDao {

  private static final ParticipantStore INSTANCE = new ParticipantStore();
  private final ParticipantDao participantDao = new ParticipantDao() {
  };
  private final RecordingDao recordingDao = new RecordingDao() {
  };
  private final Map<String, List<ParticipantItem>> participantsByUserAndTest = new LinkedHashMap<>();
  private final Map<String, ParticipantReportBean> reportsByParticipant = new LinkedHashMap<>();
  private final Map<String, String> participantNameByUserAndTest = new LinkedHashMap<>();

  private static final Logger LOGGER = Logger.getLogger(ParticipantStore.class.getName());

  private ParticipantStore() {
    try {
      recordingDao.ensureTableExists();
    } catch (Throwable ignored) {
    }
  }

  public static ParticipantStore getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized ParticipantItem registerCompletion(
      String email,
      String testName,
      LocalDateTime startedAt,
      String participantName) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    String key = normalizedEmail + "|" + normalizedTestName;
    if (participantName == null || participantName.trim().isEmpty()) {
      participantName = participantNameByUserAndTest.get(key);
    }

    ParticipantItem participant = participantDao.registerCompletion(
        email,
        testName,
        startedAt,
        participantName);
    if (participant == null) {
      return null;
    }

    LOGGER.info("registerCompletion: created participant name='" + participant.getName() + "' for user=" + email
        + ", test=" + testName);

    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return participant;
    }

    List<ParticipantItem> participants = participantsByUserAndTest.computeIfAbsent(key, k -> new ArrayList<>());
    participants.add(participant);
    participantNameByUserAndTest.remove(key);

    ParticipantReportBean report = ensureReport(
        normalizedEmail,
        normalizedTestName,
        participant.getName());
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
      String testName) {
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return new ArrayList<>();
    }

    List<ParticipantItem> participants = participantDao.listByUserAndTest(email, testName);
    participantsByUserAndTest.put(
        normalizedEmail + "|" + normalizedTestName,
        new ArrayList<>(participants));
    return new ArrayList<>(participants);
  }

  public synchronized void saveSurveyResponse(
      String email,
      String testName,
      String participantName,
      String question,
      Object answer) {
    LOGGER.info("saveSurveyResponse: email=" + email + ", test=" + testName + ", participant=" + participantName
        + ", question=" + question + ", answer=" + String.valueOf(answer));
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    String rememberedParticipantName = resolveParticipantName(
        participantName,
        1);
    participantNameByUserAndTest.put(
        normalizedEmail + "|" + normalizedTestName,
        rememberedParticipantName);
    ParticipantReportBean report = ensureReport(
        normalizedEmail,
        normalizedTestName,
        rememberedParticipantName);
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
    } catch (Exception ignore) {
    }

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

  @Override
  public synchronized boolean renameParticipant(String email, String testName, String fromName, String toName) {
    if (fromName == null || toName == null || toName.trim().isEmpty() || fromName.equals(toName.trim())) {
      return false;
    }
    boolean renamed = participantDao.renameParticipant(email, testName, fromName, toName.trim());
    recordingDao.renameRecordingParticipant(email, testName, fromName, toName.trim());

    String oldKey = normalize(email) + "|" + normalize(testName) + "|" + normalize(fromName);
    String newKey = normalize(email) + "|" + normalize(testName) + "|" + normalize(toName);
    ParticipantReportBean report = reportsByParticipant.remove(oldKey);
    if (report != null) {
      report.setParticipantName(toName.trim());
      reportsByParticipant.put(newKey, report);
    }
    String listKey = normalize(email) + "|" + normalize(testName);
    List<ParticipantItem> list = participantsByUserAndTest.get(listKey);
    if (list != null) {
      for (ParticipantItem item : list) {
        if (item.getName() != null && item.getName().equalsIgnoreCase(fromName)) {
          item.setName(toName.trim());
        }
      }
    }
    participantNameByUserAndTest.put(listKey, toName.trim());
    return renamed;
  }

  public synchronized boolean saveAudioAsset(
      String email,
      String testName,
      String participantName,
      String fileName,
      String audioUrl) {
    return saveAudioAsset(email, testName, participantName, fileName, audioUrl, QuestionNumbers.TYPE_SESSION);
  }

  public synchronized boolean saveAudioAsset(
      String email,
      String testName,
      String participantName,
      String fileName,
      String audioUrl,
      String recordingType) {
    String type = recordingType == null || recordingType.isBlank()
        ? QuestionNumbers.TYPE_SESSION
        : recordingType.trim();
    LOGGER.info("saveAudioAsset: type=" + type + ", email=" + email + ", test=" + testName
        + ", participant=" + participantName + ", fileName=" + fileName);
    String normalizedEmail = normalize(email);
    String normalizedTestName = normalize(testName);
    String rememberedParticipantName = resolveParticipantName(
        participantName,
        1);
    participantNameByUserAndTest.put(
        normalizedEmail + "|" + normalizedTestName,
        rememberedParticipantName);
    ParticipantReportBean report = ensureReport(
        normalizedEmail,
        normalizedTestName,
        rememberedParticipantName);
    if (QuestionNumbers.TYPE_MIC.equalsIgnoreCase(type)) {
      report.setMicAudioFileName(fileName);
      report.setMicAudioUrl(audioUrl);
    } else {
      report.setAudioFileName(fileName);
      report.setAudioUrl(audioUrl);
    }

    try {
      return recordingDao.saveRecording(
          email,
          testName,
          rememberedParticipantName,
          type,
          fileName,
          audioUrl,
          report.getDurationMinutes() != null ? report.getDurationMinutes() * 60 : null);
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  public synchronized ParticipantReportBean getReport(
      String email,
      String testName,
      String participantName) {
    String key = normalize(email) + "|" + normalize(testName) + "|" + normalize(participantName);
    ParticipantReportBean report = reportsByParticipant.get(key);
    if (report != null) {
      fillAudioFromStore(report, email, testName, participantName);
      return report;
    }

    List<Map<String, Object>> persisted = participantDao.listResponses(email, testName,
        participantName == null ? "" : participantName);
    if (persisted != null && !persisted.isEmpty()) {
      report = new ParticipantReportBean();
      report.setParticipantName(
          participantName == null || participantName.trim().isEmpty() ? "Participante" : participantName);
      report.setTestName(safeTestName(testName));
      report.setDescription("Reporte cargado desde la base de datos.");
      for (Map<String, Object> row : persisted) {
        String q = row.get("question") == null ? null : String.valueOf(row.get("question"));
        Object ans = row.get("answer");
        Object numericObj = row.get("numeric");
        Object audioData = row.get("audio");
        if (QuestionNumbers.isAudioName(q)) {
          applyAudioRow(report, q, ans, audioData);
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

      fillAudioFromStore(report, email, testName, participantName);

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

  private void fillAudioFromStore(
      ParticipantReportBean report,
      String email,
      String testName,
      String participantName) {
    if (report.getAudioUrl() == null || report.getAudioUrl().isEmpty()) {
      applyRecordingMap(report, recordingDao.getRecording(email, testName, participantName, QuestionNumbers.TYPE_SESSION), false);
    }
    if (report.getMicAudioUrl() == null || report.getMicAudioUrl().isEmpty()) {
      applyRecordingMap(report, recordingDao.getRecording(email, testName, participantName, QuestionNumbers.TYPE_MIC), true);
    }
  }

  private void applyAudioRow(ParticipantReportBean report, String question, Object ans, Object audioData) {
    boolean mic = QuestionNumbers.isMicAudio(QuestionNumbers.toNumber(question));
    if (audioData != null && !String.valueOf(audioData).trim().isEmpty()) {
      if (mic) {
        report.setMicAudioUrl(String.valueOf(audioData));
      } else {
        report.setAudioUrl(String.valueOf(audioData));
      }
    }
    if (ans != null && !String.valueOf(ans).trim().isEmpty()) {
      if (mic) {
        report.setMicAudioFileName(String.valueOf(ans));
      } else {
        report.setAudioFileName(String.valueOf(ans));
      }
    }
  }

  private void applyRecordingMap(ParticipantReportBean report, Map<String, Object> rec, boolean mic) {
    if (rec == null || rec.get("audioData") == null) {
      return;
    }
    if (mic) {
      report.setMicAudioUrl(String.valueOf(rec.get("audioData")));
      if (rec.get("fileName") != null) {
        report.setMicAudioFileName(String.valueOf(rec.get("fileName")));
      }
    } else {
      report.setAudioUrl(String.valueOf(rec.get("audioData")));
      if (rec.get("fileName") != null) {
        report.setAudioFileName(String.valueOf(rec.get("fileName")));
      }
    }
  }

  private ParticipantReportBean ensureReport(
      String normalizedEmail,
      String normalizedTestName,
      String participantName) {
    String normalizedParticipantName = normalize(participantName);
    String cacheKey = normalizedEmail +
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
              1));
      report.setTestName("Prueba sin nombre");
      report.setDescription("Participación iniciada para la prueba.");
      reportsByParticipant.put(cacheKey, report);
    }
    return report;
  }

  private String resolveParticipantName(
      String participantName,
      int fallbackIndex) {
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
