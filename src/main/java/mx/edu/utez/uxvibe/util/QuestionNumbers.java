package mx.edu.utez.uxvibe.util;

import java.util.Locale;

/**
 * Maps survey field names to RESPUESTAS.NUMERO_PREGUNTA and back.
 * Session audio uses 0; microphone-test audio uses 26 so they do not overwrite.
 */
public final class QuestionNumbers {
  public static final int AUDIO = 0;
  public static final int AUDIO_MIC = 26;
  public static final String TYPE_SESSION = "TEST_SESSION";
  public static final String TYPE_MIC = "MIC_TEST";
  public static final int AGE = 1;
  public static final int GENDER = 2;
  public static final int EDUCATION = 3;
  public static final int STRESS = 4;
  public static final int RELAXATION = 5;
  public static final int SATISFACTION = 6;
  public static final int IMPACT = 7;
  public static final int CONTROL = 8;

  private QuestionNumbers() {
  }

  public static boolean isAudio(int number) {
    return number == AUDIO || number == AUDIO_MIC;
  }

  public static boolean isMicAudio(int number) {
    return number == AUDIO_MIC;
  }

  public static boolean isAudioName(String question) {
    if (question == null) {
      return false;
    }
    String q = question.trim().toLowerCase(Locale.ROOT);
    return "audio".equals(q)
        || "audio_url".equals(q)
        || "audio_mic".equals(q)
        || "mic_audio".equals(q);
  }

  public static int forRecordingType(String recordingType) {
    if (recordingType != null && TYPE_MIC.equalsIgnoreCase(recordingType.trim())) {
      return AUDIO_MIC;
    }
    return AUDIO;
  }

  public static int toNumber(String question) {
    if (question == null) {
      return -1;
    }
    String q = question.trim().toLowerCase(Locale.ROOT);
    if (q.isEmpty()) {
      return -1;
    }
    if ("audio_mic".equals(q) || "mic_audio".equals(q)) {
      return AUDIO_MIC;
    }
    if (isAudioName(q)) {
      return AUDIO;
    }
    switch (q) {
      case "age":
        return AGE;
      case "gender":
        return GENDER;
      case "education":
        return EDUCATION;
      case "stress":
        return STRESS;
      case "relaxation":
        return RELAXATION;
      case "satisfaction":
        return SATISFACTION;
      case "impact":
        return IMPACT;
      case "control":
        return CONTROL;
      default:
        break;
    }
    if (q.length() > 1 && q.charAt(0) == 'q') {
      try {
        int n = Integer.parseInt(q.substring(1));
        if (n >= 1 && n <= 15) {
          return 10 + n;
        }
      } catch (NumberFormatException ignored) {
      }
    }
    return 1000 + Math.floorMod(q.hashCode(), 8000);
  }

  public static String toName(int number) {
    switch (number) {
      case AUDIO:
        return "audio";
      case AUDIO_MIC:
        return "audio_mic";
      case AGE:
        return "age";
      case GENDER:
        return "gender";
      case EDUCATION:
        return "education";
      case STRESS:
        return "stress";
      case RELAXATION:
        return "relaxation";
      case SATISFACTION:
        return "satisfaction";
      case IMPACT:
        return "impact";
      case CONTROL:
        return "control";
      default:
        break;
    }
    if (number >= 11 && number <= 25) {
      return "q" + (number - 10);
    }
    return "field_" + number;
  }
}
