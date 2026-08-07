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
    surveyResponses.add(response);
  }
}
