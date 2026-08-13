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

  private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ParticipantStore.class.getName());

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
    LOGGER.info("saveSurveyResponse called: email=" + email + ", test=" + testName + ", participant=" + participantName + ", question=" + question + ", answer=" + String.valueOf(answer));
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

    // compute numeric (already stored in report) and persist to DB via dao
    Integer numeric = null;
    try {
      java.util.Map<String, Object> last = report.getSurveyResponses().get(report.getSurveyResponses().size() - 1);
      Object numObj = last.get("numeric");
      if (numObj != null) {
        numeric = Integer.parseInt(String.valueOf(numObj));
      }
    } catch (Exception ignore) {}
    // persist via DAO
    try {
      dao.saveSurveyResponseToDb(email, testName, rememberedParticipantName, question, answer, numeric);
    } catch (Throwable t) {
      // avoid failing the request if DB not available
      t.printStackTrace();
    }
  }

  public synchronized boolean deleteByTest(String email, String testName) {
    boolean ok = dao.deleteByTest(email, testName);
    // remove cached participants and reports
    String key = normalize(email) + "|" + normalize(testName);
    participantsByUserAndTest.remove(key);
    participantNameByUserAndTest.remove(key);
    java.util.List<String> toRemove = new java.util.ArrayList<>();
    for (String k : new java.util.ArrayList<>(reportsByParticipant.keySet())) {
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
    boolean ok = dao.deleteParticipant(email, testName, participantName);
    // remove cached participant
    String key = normalize(email) + "|" + normalize(testName);
    java.util.List<ParticipantItem> list = participantsByUserAndTest.get(key);
    if (list != null) {
      list.removeIf(p -> participantName.equals(p.getName()));
    }
    // remove report
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
    LOGGER.info("saveAudioAsset called: email=" + email + ", test=" + testName + ", participant=" + participantName + ", fileName=" + fileName + ", audioUrlLength=" + (audioUrl==null?0:audioUrl.length()));
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
    // persist audio link as a special response so it survives restarts
    try {
      dao.saveSurveyResponseToDb(email, testName, rememberedParticipantName, "audio", audioUrl, null);
    } catch (Throwable ignore) {
      // don't break on DB errors
      ignore.printStackTrace();
    }
  }

  public synchronized ParticipantReportBean getReport(
    String email,
    String testName,
    String participantName
  ) {
    String key = normalize(email) + "|" + normalize(testName) + "|" + normalize(participantName);
    ParticipantReportBean report = reportsByParticipant.get(key);
    if (report != null) {
      return report;
    }

    // Try to load persisted responses from DB for this participant
    java.util.List<java.util.Map<String,Object>> persisted = dao.listResponses(email, testName, participantName == null ? "" : participantName);
    if (persisted != null && !persisted.isEmpty()) {
      report = new ParticipantReportBean();
      report.setParticipantName(participantName == null || participantName.trim().isEmpty() ? "Participante" : participantName);
      report.setTestName(safeTestName(testName));
      report.setDescription("Reporte cargado desde la base de datos.");
      // populate responses
      for (java.util.Map<String,Object> row : persisted) {
        String q = row.get("question") == null ? null : String.valueOf(row.get("question"));
        Object ans = row.get("answer");
        Object numericObj = row.get("numeric");
        if (q != null && ("audio".equalsIgnoreCase(q) || "audio_url".equalsIgnoreCase(q))) {
          // legacy: audio saved as special response
          String audioUrl = ans == null ? null : String.valueOf(ans);
          report.setAudioUrl(audioUrl);
          // attempt to set filename if present in question map (not standard)
        } else {
          // add response preserving numeric if available
          java.util.Map<String,Object> r = new java.util.LinkedHashMap<>();
          r.put("question", q);
          r.put("answer", ans);
          if (numericObj != null) {
            r.put("numeric", numericObj);
          } else {
            // recompute numeric if possible
            // use report bean helper by calling addSurveyResponse (it will compute numeric)
            report.addSurveyResponse(q, ans);
            continue;
          }
          report.getSurveyResponses().add(r);
        }
      }
      reportsByParticipant.put(key, report);
      return report;
    }

    // Fallback: if participantName is null or not found, try to find any report for this user/test
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
