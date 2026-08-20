<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.bean.ParticipantReportBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%!
  private String urlEncode(String value) {
    if (value == null) {
      return "";
    }
    try {
      return java.net.URLEncoder.encode(value, "UTF-8");
    } catch (Exception e) {
      return value;
    }
  }

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
    if ("stress".equals(question) || "relaxation".equals(question)) {
      String a = String.valueOf(rawAnswer).toLowerCase();
      switch (a) {
        case "never": case "nunca": return "Nunca (1/5)";
        case "sometimes": case "a veces": case "aveces": return "De vez en cuando (2/5)";
        case "half-time": case "medio tiempo": case "mitad del tiempo": return "Mitad del tiempo (3/5)";
        case "most-time": case "la mayor parte": case "casi siempre": return "La mayor parte (4/5)";
        case "always": case "siempre": return "Siempre (5/5)";
        default: return String.valueOf(rawAnswer);
      }
    }
    if ("gender".equals(question)) {
      String g = String.valueOf(rawAnswer).toLowerCase();
      if ("masculine".equals(g) || "masculino".equals(g)) return "Masculino";
      if ("feminine".equals(g) || "femenino".equals(g)) return "Femenino";
      return String.valueOf(rawAnswer);
    }
    if ("education".equals(question)) {
      String e = String.valueOf(rawAnswer).toLowerCase();
      switch (e) {
        case "basic": return "Básico (Primaria)";
        case "secondary": return "Medio (Secundaria)";
        case "preparatory": return "Medio Superior (Preparatoria)";
        case "university": return "Superior (Universidad)";
        case "masters": return "Superior (Maestría)";
        case "doctorate": return "Superior (Doctorado)";
        default: return String.valueOf(rawAnswer);
      }
    }
    return String.valueOf(rawAnswer);
  }

  private String getDisplayLabel(String question) {
    if (question == null) {
      return "Pregunta";
    }
    if ("age".equals(question)) return "Edad";
    if ("gender".equals(question)) return "Sexo";
    if ("education".equals(question)) return "Nivel de Educación";
    if ("stress".equals(question)) return "Frecuencia de estrés (SB-2)";
    if ("relaxation".equals(question)) return "Frecuencia de relajación (SB-2)";
    if ("satisfaction".equals(question)) return "Valencia / Satisfacción (SAM-1)";
    if ("impact".equals(question)) return "Activación / Impacto (SAM-2)";
    if ("control".equals(question)) return "Dominio / Control emocional (SAM-3)";
    if (question.startsWith("q")) {
      return "Pregunta " + question.substring(1) + " (SUS)";
    }
    return question;
  }

  private String getSummaryLabel(double average) {
    if (average >= 4.5) return "Muy alta satisfacción / Usabilidad excelente";
    if (average >= 3.5) return "Alta satisfacción / Buena usabilidad";
    if (average >= 2.5) return "Satisfacción media / Usabilidad regular";
    if (average >= 1.5) return "Baja satisfacción / Oportunidades de mejora";
    return "Muy baja / Requiere atención urgente";
  }

  private Integer parseNumeric(Map<String, Object> r) {
    if (r == null) return null;
    Object numObj = r.get("numeric");
    if (numObj != null) {
      try { return Integer.parseInt(String.valueOf(numObj)); } catch(Exception ignored) {}
    }
    Object ans = r.get("answer");
    String q = r.get("question") == null ? null : String.valueOf(r.get("question"));
    if (q != null && q.startsWith("q")) {
      return normalizeLikertScore(q, ans);
    }
    if (ans == null) return null;
    String s = String.valueOf(ans).trim();
    try { return Integer.parseInt(s); } catch(Exception ignored) {}
    String low = s.toLowerCase();
    switch(low) {
      case "never": case "nunca": return 1;
      case "sometimes": case "a veces": case "aveces": return 2;
      case "half-time": case "medio tiempo": case "mitad del tiempo": return 3;
      case "most-time": case "la mayor parte": case "casi siempre": return 4;
      case "always": case "siempre": return 5;
      default: return null;
    }
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
  String durationLabel = durationMinutes == null ? "5 min" : durationMinutes + " min";
  java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
  String completedOn = report.getCompletedOn() == null ? "Reciente" : report.getCompletedOn().format(dtf);
  String audioUrl = report.getAudioUrl();
  List<Map<String, Object>> responses = report.getSurveyResponses();

  double stressTotal = 0; int stressCount = 0;
  double samTotal = 0; int samCount = 0;
  double encuestaTotal = 0; int encuestaCount = 0;
  if (responses != null) {
    for (Map<String, Object> r : responses) {
      String q = r == null ? null : String.valueOf(r.get("question"));
      Integer numeric = parseNumeric(r);
      if (numeric == null) continue;
      if ("stress".equals(q) || "relaxation".equals(q)) { stressTotal += numeric; stressCount++; }
      else if ("satisfaction".equals(q) || "impact".equals(q) || "control".equals(q)) { samTotal += numeric; samCount++; }
      else if (q != null && q.startsWith("q")) { encuestaTotal += numeric; encuestaCount++; }
    }
  }
  double stressAvg = stressCount == 0 ? 3.0 : stressTotal / stressCount;
  double samAvg = samCount == 0 ? 6.0 : samTotal / samCount;
  double encuestaAvg = encuestaCount == 0 ? 4.0 : encuestaTotal / encuestaCount;

  double samScaled = (samAvg / 9.0 * 5.0);
  double overallAvg = 0;
  int overallParts = 0;
  if (encuestaCount > 0) { overallAvg += encuestaAvg; overallParts++; }
  if (stressCount > 0) { overallAvg += stressAvg; overallParts++; }
  if (samCount > 0) { overallAvg += samScaled; overallParts++; }
  overallAvg = overallParts == 0 ? 4.0 : overallAvg / overallParts;

  String overallLabel = String.format(java.util.Locale.US, "%.2f", overallAvg);
  String stressLabel = String.format(java.util.Locale.US, "%.2f", stressAvg);
  String samLabel = String.format(java.util.Locale.US, "%.2f", samAvg);
  String encuestaLabel = String.format(java.util.Locale.US, "%.2f", encuestaAvg);
%>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Reporte de Participante - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/reporte-participante.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
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
              <span class="reporte-summary-title">Promedio general ponderado</span>
              <span class="reporte-summary-value"><%= overallLabel %>/5</span>
              <span class="reporte-summary-note"><%= getSummaryLabel(overallAvg) %></span>
            </article>
          </div>
        </section>

        <section class="reporte-block-1">
          <h2>Gráficas de desempeño y bienestar</h2>

          <div class="reporte-graphs">
            <div class="reporte-graph-item">
              <div class="reporte-graph-title">Estrés / Bienestar (SB-2)</div>
              <div class="pie-chart" role="img" aria-label="Estrés promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(stressAvg / 5.0 * 100)) %>; --color: #FF9F80;">
                  <div class="pie-center"><span><%= stressLabel %></span><small>/5</small></div>
                </div>
              </div>
            </div>

            <div class="reporte-graph-item">
              <div class="reporte-graph-title">SAM (Promedio emocional)</div>
              <div class="pie-chart" role="img" aria-label="SAM promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(samAvg / 9.0 * 100)) %>; --color: #6FB1FF;">
                  <div class="pie-center"><span><%= samLabel %></span><small>/9</small></div>
                </div>
              </div>
            </div>

            <div class="reporte-graph-item">
              <div class="reporte-graph-title">Usabilidad (Encuestas SUS)</div>
              <div class="pie-chart" role="img" aria-label="Encuestas promedio">
                <div class="pie" style="--pct:<%= Math.min(100, (int)(encuestaAvg / 5.0 * 100)) %>; --color: #7AD29F;">
                  <div class="pie-center"><span><%= encuestaLabel %></span><small>/5</small></div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="reporte-block-1">
          <h2>Respuestas detalladas del participante</h2>
          <div class="reporte-box">
            <ul class="reporte-simple-list">
              <%
                int shown = 0;
                if (responses != null) {
                  for (Map<String, Object> r : responses) {
                    String q = r == null ? null : String.valueOf(r.get("question"));
                    Object a = r == null ? null : r.get("answer");
                    if (q == null || "audio".equalsIgnoreCase(q) || "audio_url".equalsIgnoreCase(q)) continue;
                    %>
                    <li><strong><%= getDisplayLabel(q) %>:</strong> <%= formatResponseValue(q, a) %></li>
                    <%
                    shown++;
                  }
                }
                if (shown == 0) {
              %>
                <li>No hay respuestas adicionales registradas.</li>
              <% } %>
            </ul>
          </div>
        </section>

        <section class="reporte-block-2">
          <h2>Grabación de audio de la sesión</h2>
          <% if (audioUrl != null && !audioUrl.isEmpty()) { 
               String cleanAudioSrc = audioUrl.startsWith("data:") ? audioUrl : ("data:audio/webm;base64," + audioUrl);
               String audioName = report.getAudioFileName() == null ? "grabacion-sesion.webm" : report.getAudioFileName();
          %>
            <div class="reporte-box reporte-box--summary reporte-box--audio">
              <audio controls preload="auto" class="reporte-audio-player" src="<%= cleanAudioSrc %>"></audio>
              <div class="reporte-audio-footer">
                <span class="reporte-audio-filename">Archivo: <%= audioName %></span>
                <a href="<%= cleanAudioSrc %>" download="<%= audioName %>" class="reporte-audio-download">Descargar audio (.webm)</a>
              </div>
            </div>
          <% } else { %>
            <div class="reporte-box reporte-box--summary">
              No se registró grabación de audio para esta sesión o fue una prueba sin micrófono.
            </div>
          <% } %>
        </section>
      </main>

      <a class="reporte-back" href="${pageContext.request.contextPath}/participants?testName=<%= urlEncode(testName) %>">
        <img src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-back-light.svg" alt="" />
        <span>Regresar a Participantes</span>
      </a>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/reporte-participante.js"></script>
  </body>
</html>
