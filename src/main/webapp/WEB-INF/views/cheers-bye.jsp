<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.bean.ParticipantReportBean" %>
<%
  ParticipantReportBean report = (ParticipantReportBean) request.getAttribute("report");
  String audioUrl = report != null ? report.getAudioUrl() : null;
  String testName = (String) request.getAttribute("testName");
  if (testName == null && session.getAttribute("currentTestName") != null) {
    testName = (String) session.getAttribute("currentTestName");
  }
  String participantName = (String) request.getAttribute("participantName");
  if (participantName == null && session.getAttribute("currentParticipantName") != null) {
    participantName = (String) session.getAttribute("currentParticipantName");
  }
%>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>¡Gracias por participar! - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cheers-bye.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="cheers-container">
      <main class="cheers-frame">
        <section class="cheers-header">
          <h1 class="cheers-title">¡Gracias por participar!</h1>
        </section>

        <section class="success-section">
          <div class="success-box">
            <img
              src="${pageContext.request.contextPath}/public/Cheers N Bye/lets-icons-check-fill@2x.png"
              alt="Éxito"
              class="success-icon"
              aria-label="Éxito"
            />
            <p class="success-message">
              Tus respuestas y prueba han sido procesadas correctamente, agradecemos mucho tu tiempo y colaboración.
            </p>
          </div>
        </section>

        <div class="cheers-exit-form">
          <button class="btn-exit" id="btnExitTrigger" type="button" aria-label="Salir de la prueba">
            <img
              src="${pageContext.request.contextPath}/public/Cheers N Bye/lets-icons-back-light.svg"
              alt=""
              aria-hidden="true"
            />
            <span>Salir</span>
          </button>
        </div>
      </main>
    </div>

    <!-- Evaluator Audio Check & Save Modal -->
    <div class="evaluator-modal-overlay" id="evaluatorModal" aria-hidden="true">
      <div class="evaluator-modal-card" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
        <h2 class="modal-title" id="modalTitle">Revisión del Evaluador</h2>
        <p class="modal-desc">
          Verifica la grabación de audio antes de guardar el reporte del participante <strong><%= participantName != null ? participantName : "Participante" %></strong>.
        </p>

        <div class="audio-preview-box">
          <div class="audio-preview-header">
            <span class="audio-preview-label">Grabación de la sesión:</span>
            <a id="evaluatorDownloadLink" href="#" download="grabacion-sesion.webm" class="evaluator-download-link">Descargar audio</a>
          </div>
          <audio id="evaluatorAudioPlayer" controls preload="auto" class="evaluator-audio-player" src="<%= (audioUrl != null && !audioUrl.isEmpty()) ? (audioUrl.startsWith("data:") ? audioUrl : ("data:audio/webm;base64," + audioUrl)) : "" %>">
            Tu navegador no soporta el elemento de audio.
          </audio>
          <div id="noAudioWarning" class="no-audio-warning<%= (audioUrl != null && !audioUrl.isEmpty()) ? " is-hidden" : "" %>">
            Audio almacenado temporalmente en sesión local o sin micrófono disponible.
          </div>
        </div>

        <form class="modal-actions" action="${pageContext.request.contextPath}/complete-test" method="post">
          <input type="hidden" name="testName" value="<%= testName != null ? testName : "" %>" />
          <input type="hidden" name="participantName" value="<%= participantName != null ? participantName : "" %>" />
          <button type="submit" class="btn-save-all" id="btnSaveAll">
            <span>Guardar Respuestas y Audio</span>
          </button>
          <button type="button" class="btn-modal-close" id="btnCancelModal">Volver a la pantalla de salida</button>
        </form>
      </div>
    </div>

    <script src="${pageContext.request.contextPath}/JavaScript/cheers-bye.js"></script>
  </body>
</html>
