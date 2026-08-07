<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.bean.ParticipantReportBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%!
  private boolean isInvertedSurveyQuestion(String question) {
    return "q3".equals(question) || "q9".equals(question) || "q15".equals(question);
  }

  private int normalizeLikertScore(String question, Object rawAnswer) {
    try {
      int value = Integer.parseInt(String.valueOf(rawAnswer).trim());
      if (isInvertedSurveyQuestion(question)) {
        return 6 - value;
      }
      return value;
    } catch (Exception ex) {
      return 0;
    }
  }

  private String formatResponseValue(String question, Object rawAnswer) {
    if (rawAnswer == null) {
      return "Sin respuesta";
    }
    if (question != null && question.startsWith("q")) {
      return normalizeLikertScore(question, rawAnswer) + "/5";
    }
    if ("satisfaction".equals(question) || "impact".equals(question) || "control".equals(question)) {
      return String.valueOf(rawAnswer) + "/9";
    }
    return String.valueOf(rawAnswer);
  }

  private String getDisplayLabel(String question) {
    if (question == null) {
      return "Pregunta";
    }
    if ("satisfaction".equals(question)) {
      return "Valencia (SAM)";
    }
    if ("impact".equals(question)) {
      return "Activación (SAM)";
    }
    if ("control".equals(question)) {
      return "Dominio (SAM)";
    }
    if (question.startsWith("q")) {
      return "Pregunta " + question.substring(1);
    }
    return question;
  }

  private String getSummaryLabel(double average) {
    if (average >= 4.5) {
      return "Muy alta";
    }
    if (average >= 3.5) {
      return "Alta";
    }
    if (average >= 2.5) {
      return "Media";
    }
    if (average >= 1.5) {
      return "Baja";
    }
    return "Muy baja";
  }

  private double calculateSatisfactionAverage(List<Map<String, Object>> responses) {
    double total = 0;
    int count = 0;
    if (responses != null) {
      for (Map<String, Object> response : responses) {
        String question = response == null ? null : String.valueOf(response.get("question"));
        if (question != null && question.startsWith("q")) {
          total += normalizeLikertScore(question, response.get("answer"));
          count++;
        }
      }
    }
    return count == 0 ? 0 : total / count;
  }
%>
<%
  ParticipantReportBean report = (ParticipantReportBean) request.getAttribute("report");
  if (report == null) {
    report = new ParticipantReportBean();
    report.setParticipantName("Participante");
    report.setTestName("Prueba sin nombre");
    report.setDescription("No se encontró información adicional para este participante.");
  }
  String participantName = report.getParticipantName() == null ? "Participante" : report.getParticipantName();
  String testName = report.getTestName() == null ? "Prueba sin nombre" : report.getTestName();
  String description = report.getDescription() == null ? "" : report.getDescription();
  Integer durationMinutes = report.getDurationMinutes();
  String durationLabel = durationMinutes == null ? "Sin información" : durationMinutes + " min";
  String completedOn = report.getCompletedOn() == null ? "Sin información" : report.getCompletedOn().toString();
  String audioUrl = report.getAudioUrl();
  List<Map<String, Object>> responses = report.getSurveyResponses();
  double satisfactionAverage = calculateSatisfactionAverage(responses);
  String satisfactionAverageLabel = String.format("%.1f", satisfactionAverage);
  String satisfactionSummaryLabel = getSummaryLabel(satisfactionAverage);
%>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Reporte de participante</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/reporte-participante.css" />
  </head>
  <body>
    <div class="reporte-page">
      <section class="reporte-header">
        <img class="reporte-user-icon" src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-user-light@2x.png" alt="Participante" />
        <div class="reporte-user-info">
          <div class="reporte-user-name"><%= participantName %></div>
          <div class="reporte-meta-row">
            <img class="reporte-time-icon" src="${pageContext.request.contextPath}/public/reporte-participante/weui-time-outlined.svg" alt="" />
            <span><%= durationLabel %></span>
            <span class="reporte-meta-divider" aria-hidden="true"></span>
            <span><%= completedOn %></span>
          </div>
          <div class="reporte-title-inline">Reporte de resultados</div>
        </div>
      </section>

      <main class="reporte-body">
        <section class="reporte-block-1">
          <h2>Detalles de la prueba</h2>
          <div class="reporte-box reporte-box--summary"><%= description %></div>
        </section>

        <section class="reporte-block-2">
          <h2>Información de la prueba</h2>
          <div class="reporte-box reporte-box--info">
            <table aria-label="Resumen de prueba">
              <thead>
                <tr>
                  <th>Participante</th>
                  <th>Prueba</th>
                  <th>Duración</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><%= participantName %></td>
                  <td><%= testName %></td>
                  <td><%= durationLabel %></td>
                  <td><%= completedOn %></td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <section class="reporte-block-1">
          <h2>Resumen cuantitativo</h2>
          <div class="reporte-summary-grid">
            <article class="reporte-summary-card">
              <span class="reporte-summary-title">Promedio final</span>
              <span class="reporte-summary-value"><%= satisfactionAverageLabel %>/5</span>
              <span class="reporte-summary-note"><%= satisfactionSummaryLabel %></span>
            </article>
            <article class="reporte-summary-card">
              <span class="reporte-summary-title">Respuestas registradas</span>
              <span class="reporte-summary-value"><%= responses == null ? 0 : responses.size() %></span>
              <span class="reporte-summary-note">Incluye cuestionarios y SAM</span>
            </article>
          </div>
        </section>

        <% if (responses != null && !responses.isEmpty()) { %>
          <section class="reporte-block-1">
            <h2>Respuestas registradas</h2>
            <div class="reporte-response-list">
              <% for (Map<String, Object> surveyResponse : responses) { %>
                <article class="reporte-response-item">
                  <div class="reporte-response-head">
                    <strong><%= getDisplayLabel(String.valueOf(surveyResponse.get("question"))) %></strong>
                    <span class="reporte-response-badge"><%= formatResponseValue(String.valueOf(surveyResponse.get("question")), surveyResponse.get("answer")) %></span>
                  </div>
                  <div class="reporte-response-meta">
                    <span>Marcada: <%= surveyResponse.get("answer") == null ? "Sin respuesta" : String.valueOf(surveyResponse.get("answer")) %></span>
                    <span>
                      <% if (surveyResponse.get("question") != null && String.valueOf(surveyResponse.get("question")).startsWith("q")) { %>
                        Escala: <%= isInvertedSurveyQuestion(String.valueOf(surveyResponse.get("question"))) ? "valor invertido" : "valor directo" %>
                      <% } else { %>
                        Escala: SAM / 9
                      <% } %>
                    </span>
                  </div>
                </article>
              <% } %>
            </div>
          </section>
        <% } else { %>
          <section class="reporte-block-1">
            <h2>Respuestas registradas</h2>
            <div class="reporte-box reporte-box--summary">No se registraron respuestas de encuesta para este participante.</div>
          </section>
        <% } %>

        <% if (audioUrl != null && !audioUrl.isEmpty()) { %>
          <section class="reporte-block-2">
            <h2>Grabación de audio</h2>
            <div class="reporte-box reporte-box--summary reporte-box--audio">
              <audio controls preload="metadata" src="data:audio/webm;base64,<%= audioUrl %>"></audio>
              <p class="reporte-audio-filename">Archivo: <%= report.getAudioFileName() == null ? "Sin nombre" : report.getAudioFileName() %></p>
            </div>
          </section>
        <% } else { %>
          <section class="reporte-block-2">
            <h2>Grabación de audio</h2>
            <div class="reporte-box reporte-box--summary">No se registró ninguna grabación para este participante.</div>
          </section>
        <% } %>
      </main>

      <a class="reporte-back" href="${pageContext.request.contextPath}/participants">
        <img src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-back-light.svg" alt="" />
        <span>Regresar</span>
      </a>
    </div>
  </body>
</html>
