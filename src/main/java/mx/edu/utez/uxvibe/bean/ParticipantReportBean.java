package mx.edu.utez.uxvibe.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

  public List<Map<String, Object>> getSurveyResponses() {
    return surveyResponses;
  }

  public void addSurveyResponse(String question, Object answer) {
    Map<String, Object> response = new java.util.LinkedHashMap<>();
    response.put("question", question);
    response.put("answer", answer);
    // compute numeric equivalent
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
      // Likert questions q1..qN (1-5)
      if (question != null && question.startsWith("q")) {
        int v = Integer.parseInt(answer);
        // inverted questions q3, q9, q15 -> invert 1..5 => 6 - value
        if ("q3".equals(question) || "q9".equals(question) || "q15".equals(question)) {
          return 6 - v;
        }
        return v;
      }
      // SAM questions (satisfaction, impact, control) range 1-9
      if ("satisfaction".equals(question) || "impact".equals(question) || "control".equals(question)) {
        return Integer.parseInt(answer);
      }
      // Stress questions use textual options: support English and Spanish labels -> map 1..5
      if ("stress".equals(question) || "relaxation".equals(question)) {
        String a = answer.toLowerCase();
        switch (a) {
          // English
          case "never":
            return 1;
          case "sometimes":
            return 2;
          case "half-time":
          case "half time":
          case "half_time":
            return 3;
          case "most-time":
          case "most time":
          case "most_time":
            return 4;
          case "always":
            return 5;
          // Spanish variants
          case "nunca":
            return 1;
          case "a veces":
          case "aveces":
          case "a_veces":
            return 2;
          case "medio tiempo":
          case "medio-tiempo":
          case "medio_tiempo":
          case "mitad del tiempo":
            return 3;
          case "la mayor parte":
          case "la mayor parte del tiempo":
          case "casi siempre":
          case "frecuentemente":
            return 4;
          case "siempre":
            return 5;
          default:
            return null;
        }
      }
      // Age stored as numeric
      if ("age".equals(question)) {
        return Integer.parseInt(answer);
      }
    } catch (Exception ex) {
      return null;
    }
    return null;
  }
}
