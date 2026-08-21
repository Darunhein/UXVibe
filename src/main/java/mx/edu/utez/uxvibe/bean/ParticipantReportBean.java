package mx.edu.utez.uxvibe.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParticipantReportBean {

  private String participantName;
  private String testName;
  private String description;
  private Integer durationMinutes;
  private LocalDateTime completedOn;
  private String audioFileName;
  private String audioUrl;
  private String micAudioFileName;
  private String micAudioUrl;
  private Integer age;
  private String gender;
  private String education;
  private final List<Map<String, Object>> surveyResponses = new ArrayList<>();

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public LocalDateTime getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn(LocalDateTime completedOn) {
    this.completedOn = completedOn;
  }

  public String getAudioFileName() {
    return audioFileName;
  }

  public void setAudioFileName(String audioFileName) {
    this.audioFileName = audioFileName;
  }

  public String getAudioUrl() {
    return audioUrl;
  }

  public void setAudioUrl(String audioUrl) {
    this.audioUrl = audioUrl;
  }

  public String getMicAudioFileName() {
    return micAudioFileName;
  }

  public void setMicAudioFileName(String micAudioFileName) {
    this.micAudioFileName = micAudioFileName;
  }

  public String getMicAudioUrl() {
    return micAudioUrl;
  }

  public void setMicAudioUrl(String micAudioUrl) {
    this.micAudioUrl = micAudioUrl;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public List<Map<String, Object>> getSurveyResponses() {
    return surveyResponses;
  }

  public static boolean isInvertedQuestion(String question) {
    return "q5".equals(question) || "q9".equals(question) || "q15".equals(question);
  }

  public void addSurveyResponse(String question, Object answer) {
    if ("age".equals(question) && answer != null) {
      try {
        this.age = Integer.parseInt(String.valueOf(answer).trim());
      } catch (Exception ignored) {
      }
    } else if ("gender".equals(question) && answer != null) {
      this.gender = String.valueOf(answer).trim();
    } else if ("education".equals(question) && answer != null) {
      this.education = String.valueOf(answer).trim();
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("question", question);
    response.put("answer", answer);
    Integer numeric = computeNumericEquivalent(question, answer);
    response.put("numeric", numeric);
    surveyResponses.add(response);
  }

  private Integer computeNumericEquivalent(String question, Object rawAnswer) {
    if (rawAnswer == null) {
      return null;
    }
    String answer = String.valueOf(rawAnswer).trim();
    try {
      if (question != null && question.startsWith("q")) {
        int v = Integer.parseInt(answer);
        if (isInvertedQuestion(question)) {
          return 6 - v;
        }
        return v;
      }
      if ("satisfaction".equals(question) || "impact".equals(question) || "control".equals(question)) {
        return Integer.parseInt(answer);
      }
      if ("stress".equals(question) || "relaxation".equals(question)) {
        String a = answer.toLowerCase();
        switch (a) {
          case "never":
          case "nunca":
            return 1;
          case "sometimes":
          case "a veces":
          case "aveces":
          case "a_veces":
            return 2;
          case "half-time":
          case "half time":
          case "half_time":
          case "medio tiempo":
          case "medio-tiempo":
          case "medio_tiempo":
          case "mitad del tiempo":
            return 3;
          case "most-time":
          case "most time":
          case "most_time":
          case "la mayor parte":
          case "la mayor parte del tiempo":
          case "casi siempre":
          case "frecuentemente":
            return 4;
          case "always":
          case "siempre":
            return 5;
          default:
            return null;
        }
      }
      if ("age".equals(question)) {
        return Integer.parseInt(answer);
      }
    } catch (Exception ex) {
      return null;
    }
    return null;
  }
}
