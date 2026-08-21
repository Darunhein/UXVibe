<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.bean.ParticipantReportBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="mx.edu.utez.uxvibe.util.HtmlEscape" %>
<%! 
  private String urlEncode(String value) { 
    if (value == null) { return ""; } 
    try { 
      return java.net.URLEncoder.encode(value, "UTF-8"); 
    } catch (Exception e) { 
      return value; 
    } 
  }

  private String escapeHtml(String value) {
    return HtmlEscape.text(value);
  } 

  private boolean isInvertedSurveyQuestion(String question) { 
    return mx.edu.utez.uxvibe.bean.ParticipantReportBean.isInvertedQuestion(question); 
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
    if (rawAnswer == null) { return "Sin respuesta"; } 
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
    if (question == null) { return "Pregunta"; } 
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
  String micAudioUrl = report.getMicAudioUrl(); 
  List<Map<String, Object>> responses = report.getSurveyResponses();
                double stressTotal = 0; int stressCount = 0;
                double samTotal = 0; int samCount = 0;
                double encuestaTotal = 0; int encuestaCount = 0;
                double satisfactionScore = 0; int satisfactionCount = 0;
                double impactScore = 0; int impactCount = 0;
                double controlScore = 0; int controlCount = 0;

                if (responses != null) {
                  for (Map<String, Object> r : responses) {
                    String q = r == null ? null : String.valueOf(r.get("question"));
                    Integer numeric = parseNumeric(r);
                    if (numeric == null) continue;
                    if ("stress".equals(q) || "relaxation".equals(q)) { stressTotal += numeric; stressCount++; }
                    else if ("satisfaction".equals(q)) { satisfactionScore += numeric; satisfactionCount++; samTotal += numeric; samCount++; }
                    else if ("impact".equals(q)) { impactScore += numeric; impactCount++; samTotal += numeric; samCount++; }
                    else if ("control".equals(q)) { controlScore += numeric; controlCount++; samTotal += numeric; samCount++; }
                    else if (q != null && q.startsWith("q")) { encuestaTotal += numeric; encuestaCount++; }
                  }
                }
                double stressAvg = stressCount == 0 ? 0.0 : stressTotal / stressCount;
                double samAvg = samCount == 0 ? 0.0 : samTotal / samCount;
                double encuestaAvg = encuestaCount == 0 ? 0.0 : encuestaTotal / encuestaCount;

                double satisfactionAvg = satisfactionCount == 0 ? (samCount == 0 ? 5.0 : samAvg) : satisfactionScore / satisfactionCount;
                double impactAvg = impactCount == 0 ? (samCount == 0 ? 5.0 : samAvg) : impactScore / impactCount;
                double controlAvg = controlCount == 0 ? (samCount == 0 ? 5.0 : samAvg) : controlScore / controlCount;

                double samScaled = samCount == 0 ? 0.0 : (samAvg / 9.0 * 5.0);
                double overallAvg = 0;
                int overallParts = 0;
                if (encuestaCount > 0) { overallAvg += encuestaAvg; overallParts++; }
                if (stressCount > 0) { overallAvg += stressAvg; overallParts++; }
                if (samCount > 0) { overallAvg += samScaled; overallParts++; }
                overallAvg = overallParts == 0 ? 0.0 : overallAvg / overallParts;

                boolean hasOverall = overallParts > 0;
                String overallLabel = hasOverall ? String.format(java.util.Locale.US, "%.2f", overallAvg) : "—";
                String stressLabel = stressCount == 0 ? "—" : String.format(java.util.Locale.US, "%.2f", stressAvg);
                String samLabel = samCount == 0 ? "—" : String.format(java.util.Locale.US, "%.2f", samAvg);
                String encuestaLabel = encuestaCount == 0 ? "—" : String.format(java.util.Locale.US, "%.2f", encuestaAvg);
                %>
                <!doctype html>
                <html lang="es">

                <head>
                  <meta charset="utf-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>Reporte de Participante - UX Vibe</title>
                  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
                  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/reporte-participante.css" />
                  <link rel="stylesheet"
                    href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
                </head>

                <body>
                  <div class="reporte-page">
                    <section class="reporte-header">
                      <img class="reporte-user-icon"
                        src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-user-light@2x.png"
                        alt="Participante" />
                      <div class="reporte-user-info">
                        <div class="reporte-user-name">
                          <%= escapeHtml(participantName) %>
                        </div>
                        <div class="reporte-meta-row">
                          <img class="reporte-time-icon"
                            src="${pageContext.request.contextPath}/public/reporte-participante/weui-time-outlined.svg"
                            alt="" />
                          <span>
                            <%= durationLabel %>
                          </span>
                          <span class="reporte-meta-divider" aria-hidden="true"></span>
                          <span>
                            <%= completedOn %>
                          </span>
                        </div>
                        <div class="reporte-title-inline">Reporte de resultados</div>
                      </div>
                    </section>

                    <main class="reporte-body">
                      <section class="reporte-block-1">
                        <h2>Detalles de la prueba</h2>
                        <div class="reporte-box reporte-box--summary">
                          <%= escapeHtml(description) %>
                        </div>
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
                                <td>
                                  <%= escapeHtml(participantName) %>
                                </td>
                                <td>
                                  <%= escapeHtml(testName) %>
                                </td>
                                <td>
                                  <%= durationLabel %>
                                </td>
                                <td>
                                  <%= completedOn %>
                                </td>
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
                            <span class="reporte-summary-value">
                              <%= hasOverall ? overallLabel + "/5" : "Sin datos" %>
                            </span>
                            <span class="reporte-summary-note">
                              <%= hasOverall ? getSummaryLabel(overallAvg) : "Aún no hay respuestas cuantitativas." %>
                            </span>
                          </article>
                        </div>
                      </section>

                      <section class="reporte-block-1">
                        <h2>Gráficas de desempeño y bienestar</h2>

                        <div class="reporte-box reporte-box--analytics">
                          <div class="reporte-charts-visual-grid">
                            <!-- 1. Pie Chart: Estrés / Bienestar (SB-2) -->
                            <div class="visual-chart-card">
                              <div class="visual-chart-header">
                                <span class="visual-chart-badge badge--pie">SB-2</span>
                                <h3 class="visual-chart-title">Estrés y Bienestar</h3>
                              </div>
                              <div class="pie-visual-container">
                                <% 
                                  int stressPct = Math.max(5, Math.min(100, (int)(stressAvg / 5.0 * 100)));
                                  String pieColor = stressAvg <= 2.5 ? "#10b981" : (stressAvg <= 3.8 ? "#f59e0b" : "#ef4444");
                                %>
                                <div class="minimal-pie-gauge" style="--pct: <%= stressPct %>%; --color: <%= pieColor %>;">
                                  <div class="minimal-pie-center">
                                    <span class="pie-val"><%= stressLabel %></span>
                                    <span class="pie-denom">/ 5</span>
                                  </div>
                                </div>
                              </div>
                              <div class="chart-legend-row">
                                <span class="legend-dot" style="background: <%= pieColor %>;"></span>
                                <span class="legend-text">
                                  <%= stressAvg <= 2.5 ? "Relajado / Controlado" : (stressAvg <= 3.8 ? "Tensión Moderada" : "Estrés Elevado") %>
                                </span>
                              </div>
                            </div>

                            <!-- 2. Spider / Radar Chart: SAM (Satisfacción, Impacto, Control) -->
                            <div class="visual-chart-card">
                              <div class="visual-chart-header">
                                <span class="visual-chart-badge badge--radar">SAM</span>
                                <h3 class="visual-chart-title">Perfil Emocional (Radar)</h3>
                              </div>
                              <div class="spider-visual-container">
                                <%
                                  // Radar triangle coordinates centered at (100, 100) with radius 70
                                  // Top vertex (Control): angle -90 deg -> (100, 100 - r)
                                  // Bottom-right vertex (Satisfacción): angle 30 deg -> (100 + r*cos(30), 100 + r*sin(30))
                                  // Bottom-left vertex (Impacto): angle 150 deg -> (100 - r*cos(30), 100 + r*sin(30))
                                  double rControl = (controlAvg / 9.0) * 65.0;
                                  double rSat = (satisfactionAvg / 9.0) * 65.0;
                                  double rImp = (impactAvg / 9.0) * 65.0;

                                  double cx = 100.0, cy = 95.0;
                                  double ptControlX = cx;
                                  double ptControlY = cy - rControl;

                                  double cos30 = 0.8660;
                                  double sin30 = 0.5000;

                                  double ptSatX = cx + (rSat * cos30);
                                  double ptSatY = cy + (rSat * sin30);

                                  double ptImpX = cx - (rImp * cos30);
                                  double ptImpY = cy + (rImp * sin30);
                                %>
                                <svg class="spider-svg" viewBox="0 0 200 190" aria-label="Gráfica Spider SAM">
                                  <!-- Background Web Levels -->
                                  <polygon points="100,30 156,128 44,128" class="spider-grid-outer" />
                                  <polygon points="100,52 137,117 63,117" class="spider-grid-mid" />
                                  <polygon points="100,73 118,106 82,106" class="spider-grid-inner" />
                                  <!-- Axis lines -->
                                  <line x1="100" y1="95" x2="100" y2="30" class="spider-axis" />
                                  <line x1="100" y1="95" x2="156" y2="128" class="spider-axis" />
                                  <line x1="100" y1="95" x2="44" y2="128" class="spider-axis" />
                                  <!-- Radar Filled Polygon -->
                                  <polygon points="<%= String.format(java.util.Locale.US, "%.1f,%.1f %.1f,%.1f %.1f,%.1f", ptControlX, ptControlY, ptSatX, ptSatY, ptImpX, ptImpY) %>" class="spider-data-polygon" />
                                  <!-- Radar Vertices -->
                                  <circle cx="<%= String.format(java.util.Locale.US, "%.1f", ptControlX) %>" cy="<%= String.format(java.util.Locale.US, "%.1f", ptControlY) %>" r="4.5" class="spider-point" />
                                  <circle cx="<%= String.format(java.util.Locale.US, "%.1f", ptSatX) %>" cy="<%= String.format(java.util.Locale.US, "%.1f", ptSatY) %>" r="4.5" class="spider-point" />
                                  <circle cx="<%= String.format(java.util.Locale.US, "%.1f", ptImpX) %>" cy="<%= String.format(java.util.Locale.US, "%.1f", ptImpY) %>" r="4.5" class="spider-point" />
                                  <!-- Axis Labels -->
                                  <text x="100" y="20" text-anchor="middle" class="spider-label">Control</text>
                                  <text x="165" y="142" text-anchor="start" class="spider-label">Valencia</text>
                                  <text x="35" y="142" text-anchor="end" class="spider-label">Impacto</text>
                                </svg>
                              </div>
                              <div class="chart-legend-row">
                                <span class="legend-dot" style="background: #2563eb;"></span>
                                <span class="legend-text">Promedio: <%= samLabel %> / 9.0</span>
                              </div>
                            </div>

                            <!-- 3. Colorful Bar Chart: Usabilidad (SUS) -->
                            <div class="visual-chart-card">
                              <div class="visual-chart-header">
                                <span class="visual-chart-badge badge--bar">SUS</span>
                                <h3 class="visual-chart-title">Usabilidad y Facilidad</h3>
                              </div>
                              <div class="bar-visual-container">
                                <%
                                  int susPct = Math.max(5, Math.min(100, (int)(encuestaAvg / 5.0 * 100)));
                                %>
                                <div class="sus-column-visual">
                                  <div class="sus-column-track">
                                    <div class="sus-column-fill" style="height: <%= susPct %>%;">
                                      <span class="sus-column-value"><%= encuestaLabel %></span>
                                    </div>
                                  </div>
                                </div>
                                <div class="sus-benchmarks">
                                  <div class="sus-bench-item <%= encuestaAvg >= 4.0 ? "is-active" : "" %>">
                                    <span class="sus-dot" style="background: #10b981;"></span>
                                    <span>Excelente (4-5)</span>
                                  </div>
                                  <div class="sus-bench-item <%= (encuestaAvg >= 3.0 && encuestaAvg < 4.0) ? "is-active" : "" %>">
                                    <span class="sus-dot" style="background: #3b82f6;"></span>
                                    <span>Aceptable (3-4)</span>
                                  </div>
                                  <div class="sus-bench-item <%= (encuestaAvg < 3.0 && encuestaCount > 0) ? "is-active" : "" %>">
                                    <span class="sus-dot" style="background: #f43f5e;"></span>
                                    <span>Fricción (1-3)</span>
                                  </div>
                                </div>
                              </div>
                              <div class="chart-legend-row">
                                <span class="legend-dot" style="background: #10b981;"></span>
                                <span class="legend-text"><%= susPct %>% Usabilidad Global</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </section>

                    <section class="reporte-block-1">
                        <h2>Respuestas detalladas del participante</h2>
                        <div class="reporte-box">
                          <ul class="reporte-simple-list">
                            <% int shown=0; if (responses !=null) { for (Map<String, Object> r : responses) {
                              String q = r == null ? null : String.valueOf(r.get("question"));
                              Object a = r == null ? null : r.get("answer");
                              if (q == null || "audio".equalsIgnoreCase(q) || "audio_url".equalsIgnoreCase(q)
                                  || "audio_mic".equalsIgnoreCase(q) || "mic_audio".equalsIgnoreCase(q)) continue;
                              %>
                              <li><strong>
                                  <%= escapeHtml(getDisplayLabel(q)) %>:
                                </strong>
                                <%= escapeHtml(formatResponseValue(q, a)) %>
                              </li>
                              <% shown++; } } if (shown==0) { %>
                                <li>No hay respuestas adicionales registradas.</li>
                                <% } %>
                          </ul>
                        </div>
                      </section>

                      <section class="reporte-block-2">
                        <h2>Prueba de micrófono</h2>
                        <% if (micAudioUrl !=null && !micAudioUrl.isEmpty()) { String
                          cleanMicSrc=micAudioUrl.startsWith("data:") ? micAudioUrl : ("data:audio/webm;base64," +
                          micAudioUrl); String micName=report.getMicAudioFileName()==null ? "prueba-microfono.webm" :
                          report.getMicAudioFileName(); %>
                          <div class="reporte-box reporte-box--summary reporte-box--audio">
                            <audio controls preload="auto" class="reporte-audio-player"
                              src="<%= escapeHtml(cleanMicSrc) %>"></audio>
                            <div class="reporte-audio-footer">
                              <span class="reporte-audio-filename">Archivo: <%= escapeHtml(micName) %></span>
                              <a href="<%= escapeHtml(cleanMicSrc) %>" download="<%= escapeHtml(micName) %>"
                                class="reporte-audio-download">Descargar audio (.webm)</a>
                            </div>
                          </div>
                          <% } else { %>
                            <div class="reporte-box reporte-box--summary">
                              No se registró audio de prueba de micrófono para esta sesión.
                            </div>
                            <% } %>
                      </section>

                      <section class="reporte-block-2">
                        <h2>Grabación de audio de la sesión</h2>
                        <% if (audioUrl !=null && !audioUrl.isEmpty()) { String
                          cleanAudioSrc=audioUrl.startsWith("data:") ? audioUrl : ("data:audio/webm;base64," +
                          audioUrl); String audioName=report.getAudioFileName()==null ? "grabacion-sesion.webm" :
                          report.getAudioFileName(); %>
                          <div class="reporte-box reporte-box--summary reporte-box--audio">
                            <audio controls preload="auto" class="reporte-audio-player"
                              src="<%= escapeHtml(cleanAudioSrc) %>"></audio>
                            <div class="reporte-audio-footer">
                              <span class="reporte-audio-filename">Archivo: <%= escapeHtml(audioName) %></span>
                              <a href="<%= escapeHtml(cleanAudioSrc) %>" download="<%= escapeHtml(audioName) %>"
                                class="reporte-audio-download">Descargar audio (.webm)</a>
                            </div>
                          </div>
                          <% } else { %>
                            <div class="reporte-box reporte-box--summary">
                              No se registró grabación de audio para esta sesión o fue una prueba sin micrófono.
                            </div>
                            <% } %>
                      </section>
                    </main>

                    <a class="reporte-back"
                      href="${pageContext.request.contextPath}/participants?testName=<%= urlEncode(testName) %>">
                      <img
                        src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-back-light.svg"
                        alt="" />
                      <span>Regresar a Participantes</span>
                    </a>
                  </div>
                  <script src="${pageContext.request.contextPath}/JavaScript/reporte-participante.js"></script>
                </body>

                </html>