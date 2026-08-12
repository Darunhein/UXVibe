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

        <section class="reporte-block-1">
          <h2>Gráficas rápidas</h2>
          <%
            // calculate stress average (sb2)
            double stressTotal = 0; int stressCount = 0;
            double samTotal = 0; int samCount = 0;
            double encuestaTotal = 0; int encuestaCount = 0;
            if (responses != null) {
              for (Map<String, Object> r : responses) {
                String q = r == null ? null : String.valueOf(r.get("question"));
                Object ans = r == null ? null : r.get("answer");
                Integer numeric = null;
                try {
                  Object numObj = r.get("numeric");
                  if (numObj != null) {
                    numeric = Integer.parseInt(String.valueOf(numObj));
                  } else if (ans != null) {
                    numeric = Integer.parseInt(String.valueOf(ans));
                  }
                } catch (Exception ignore) {}
                if (numeric == null) continue;

                if ("stress".equals(q) || "relaxation".equals(q)) {
                  stressTotal += numeric; stressCount++;
                } else if ("satisfaction".equals(q) || "impact".equals(q) || "control".equals(q)) {
                  samTotal += numeric; samCount++;
                } else if (q != null && q.startsWith("q")) {
                  encuestaTotal += numeric; encuestaCount++;
                }
              }
            }
            double stressAvg = stressCount == 0 ? 0 : stressTotal / stressCount;
            double samAvg = samCount == 0 ? 0 : samTotal / samCount;
            double encuestaAvg = encuestaCount == 0 ? 0 : encuestaTotal / encuestaCount;
            String stressLabel = String.format("%.2f", stressAvg);
            String samLabel = String.format("%.2f", samAvg);
            String encuestaLabel = String.format("%.2f", encuestaAvg);
          %>

          <div class="reporte-graphs">
            <div class="reporte-graph-item">
              <div class="reporte-graph-title">Estrés (SB2)</div>
              <div class="pie-chart" role="img" aria-label="Estrés promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(stressAvg / 5.0 * 100)) %>; --color: #FF9F80;">
                  <div class="pie-center"><span><%= stressLabel %></span><small>/5</small></div>
                </div>
              </div>
            </div>

            <div class="reporte-graph-item">
              <div class="reporte-graph-title">SAM (promedio)</div>
              <div class="pie-chart" role="img" aria-label="SAM promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(samAvg / 9.0 * 100)) %>; --color: #6FB1FF;">
                  <div class="pie-center"><span><%= samLabel %></span><small>/9</small></div>
                </div>
              </div>
            </div>

            <div class="reporte-graph-item">
              <div class="reporte-graph-title">Encuestas (últimas 3 partes)</div>
              <div class="pie-chart" role="img" aria-label="Encuestas promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(encuestaAvg / 5.0 * 100)) %>; --color: #7AD29F;">
                  <div class="pie-center"><span><%= encuestaLabel %></span><small>/5</small></div>
                </div>
              </div>
            </div>
          </div>

          <style>
            .reporte-graphs{display:flex;gap:24px;margin-top:12px}
            .reporte-graph-item{flex:1;display:flex;flex-direction:column;align-items:center}
            .reporte-graph-title{font-size:14px;margin-bottom:8px}
            .pie-chart{width:120px;height:120px;display:flex;align-items:center;justify-content:center}
            .pie{width:100%;height:100%;border-radius:50%;background:conic-gradient(var(--color) var(--pct,0)%, #eee 0);display:flex;align-items:center;justify-content:center;position:relative;box-shadow:0 2px 6px rgba(0,0,0,0.06)}
            .pie::before{content:"";position:absolute;width:70%;height:70%;background:white;border-radius:50%}
            .pie-center{position:relative;text-align:center;font-weight:700}
            .pie-center small{display:block;font-weight:600;font-size:12px;color:#666;margin-top:4px}
          </style>
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
  <script src="${pageContext.request.contextPath}/JavaScript/text-only-validation.js"></script>
</html>
