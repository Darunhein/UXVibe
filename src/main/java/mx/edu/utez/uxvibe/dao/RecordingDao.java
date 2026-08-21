package mx.edu.utez.uxvibe.dao;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.edu.utez.uxvibe.util.QuestionNumbers;

public interface RecordingDao {
  Logger LOGGER = Logger.getLogger(RecordingDao.class.getName());

  Map<String, Map<String, Object>> IN_MEMORY_RECORDINGS = new LinkedHashMap<>();

  default void ensureTableExists() {
    // Audio lives in RESPUESTAS.AUDIO; the table is owned by the database schema.
  }

  default boolean saveRecording(
      String email,
      String testName,
      String participantName,
      String recordingType,
      String fileName,
      String audioBase64,
      Integer durationSeconds) {
    String key = memoryKey(email, testName, participantName, recordingType);
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("email", normalizeEmail(email));
    data.put("testName", testName);
    data.put("participantName", participantName);
    data.put("recordingType", recordingType);
    data.put("fileName", fileName);
    data.put("audioData", audioBase64);
    data.put("durationSeconds", durationSeconds);
    IN_MEMORY_RECORDINGS.put(key, data);

    int questionNumber = QuestionNumbers.forRecordingType(recordingType);
    boolean saved = new ParticipantDao() {
    }.saveAudioToDb(email, testName, participantName, fileName, audioBase64, questionNumber);
    if (saved) {
      LOGGER.info("Saved recording into RESPUESTAS.AUDIO: key=" + key + " question=" + questionNumber);
      return true;
    }
    LOGGER.log(Level.WARNING, "Database save recording failed: key=" + key);
    return false;
  }

  default Map<String, Object> getRecording(
      String email,
      String testName,
      String participantName,
      String recordingType) {
    Map<String, Object> fromDb = new ParticipantDao() {
    }.getAudioFromDb(email, testName, participantName, QuestionNumbers.forRecordingType(recordingType));
    if (fromDb != null && fromDb.get("audioData") != null) {
      return fromDb;
    }
    return IN_MEMORY_RECORDINGS.get(memoryKey(email, testName, participantName, recordingType));
  }

  default boolean deleteByTest(String email, String testName) {
    String prefix = normalizeEmail(email) + "|" + normalizeText(testName) + "|";
    IN_MEMORY_RECORDINGS.keySet().removeIf(k -> k.startsWith(prefix));
    return true;
  }

  default boolean deleteByParticipant(String email, String testName, String participantName) {
    String prefix = normalizeEmail(email) + "|" + normalizeText(testName) + "|" + normalizeText(participantName) + "|";
    IN_MEMORY_RECORDINGS.keySet().removeIf(k -> k.startsWith(prefix));
    return true;
  }

  default boolean renameRecordingParticipant(String email, String testName, String fromName, String toName) {
    if (fromName == null || toName == null || toName.trim().isEmpty() || fromName.equals(toName.trim())) {
      return false;
    }
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = normalizeText(testName);
    String fromKey = normalizedEmail + "|" + normalizedTestName + "|" + normalizeText(fromName) + "|";
    String toPrefix = normalizedEmail + "|" + normalizedTestName + "|" + normalizeText(toName) + "|";
    Map<String, Map<String, Object>> moved = new LinkedHashMap<>();
    IN_MEMORY_RECORDINGS.keySet().removeIf(k -> {
      if (k.startsWith(fromKey)) {
        Map<String, Object> data = IN_MEMORY_RECORDINGS.get(k);
        if (data != null) {
          data.put("participantName", toName.trim());
          String suffix = k.substring(fromKey.length());
          moved.put(toPrefix + suffix, data);
        }
        return true;
      }
      return false;
    });
    IN_MEMORY_RECORDINGS.putAll(moved);
    return true;
  }

  private static String memoryKey(String email, String testName, String participantName, String recordingType) {
    return normalizeEmail(email) + "|" + normalizeText(testName) + "|" + normalizeText(participantName) + "|"
        + normalizeText(recordingType);
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }

  private static String normalizeText(String text) {
    return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
  }
}
