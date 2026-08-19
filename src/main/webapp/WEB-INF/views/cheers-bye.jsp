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
    <style>
      .evaluator-modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background: rgba(15, 23, 42, 0.7);
        backdrop-filter: blur(6px);
        display: none;
        align-items: center;
        justify-content: center;
        z-index: 9999;
        padding: 20px;
      }
      .evaluator-modal-overlay.active {
        display: flex;
      }
      .evaluator-modal-card {
        background: #ffffff;
        border-radius: 16px;
        max-width: 520px;
        width: 100%;
        padding: 32px;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
        animation: modalFadeIn 0.25s ease-out;
        text-align: center;
      }
      @keyframes modalFadeIn {
        from { opacity: 0; transform: scale(0.95); }
        to { opacity: 1; transform: scale(1); }
      }
      .modal-title {
        font-size: 20px;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 8px;
      }
      .modal-desc {
        font-size: 14px;
        color: #64748b;
        margin-bottom: 20px;
      }
      .audio-preview-box {
        background: #f8fafc;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        padding: 16px;
        margin-bottom: 24px;
      }
      .audio-preview-box audio {
        width: 100%;
        margin-top: 8px;
      }
      .modal-actions {
        display: flex;
        flex-direction: column;
        gap: 12px;
      }
      .btn-save-all {
        background: #2563eb;
        color: #ffffff;
        font-weight: 600;
        padding: 14px 20px;
        border-radius: 10px;
        border: none;
        cursor: pointer;
        font-size: 15px;
        transition: background 0.2s;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
      }
      .btn-save-all:hover {
        background: #1d4ed8;
      }
      .btn-modal-close {
        background: transparent;
        color: #64748b;
        border: none;
        cursor: pointer;
        font-size: 13px;
        text-decoration: underline;
      }
    </style>
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
          <span style="font-size:13px;font-weight:600;color:#475569;">Grabación de la prueba:</span>
          <audio id="evaluatorAudioPlayer" controls preload="metadata" src="<%= (audioUrl != null && !audioUrl.isEmpty()) ? (audioUrl.startsWith("data:") ? audioUrl : ("data:audio/webm;base64," + audioUrl)) : "" %>">
            Tu navegador no soporta el elemento de audio.
          </audio>
          <div id="noAudioWarning" style="<%= (audioUrl != null && !audioUrl.isEmpty()) ? "display:none;" : "" %>color:#e11d48;font-size:12px;margin-top:6px;">
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
