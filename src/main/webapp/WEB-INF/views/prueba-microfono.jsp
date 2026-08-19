<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Prueba de Micrófono - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/prueba-microfono.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="microphone-test-container">
      <main class="microphone-test-frame">
        <section class="test-header">
          <div class="header-content">
            <div class="title-with-icon">
              <h1 class="test-title">Prueba de micrófono</h1>
              <img
                class="microphone-icon"
                src="${pageContext.request.contextPath}/public/Prueba de Microfono/icon-park-outline-microphone@2x.png"
                alt="Microphone icon"
              />
            </div>
            <h2 class="test-subtitle">Habla al micrófono para verificar tu volumen y claridad</h2>
          </div>
        </section>

        <div class="status-section">
          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-1@2x.png"
              alt="Status icon"
            />
            <div class="status-text">
              <h3 class="status-label">Estado</h3>
              <p class="status-value">Iniciando micrófono...</p>
            </div>
          </div>

          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-2@2x.png"
              alt="Duration icon"
            />
            <div class="status-text">
              <h3 class="status-label">Duración</h3>
              <p class="status-value" id="test-duration">00:00</p>
            </div>
          </div>

          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-3@2x.png"
              alt="Device icon"
            />
            <div class="status-text">
              <h3 class="status-label">Dispositivo</h3>
              <p class="status-value" id="device-name">Detectando...</p>
            </div>
          </div>
        </div>

        <div class="waveform-container" aria-live="polite">
          <!-- Waveform animated bars generated via JS -->
        </div>

        <div class="microphone-controls">
          <button id="btn-record" class="btn-primary" type="button">
            <span class="rec-dot"></span>
            <span class="btn-text">Grabar prueba</span>
          </button>
          <button id="btn-play" class="btn-secondary" type="button" disabled>
            <span class="play-icon">▶</span>
            <span class="btn-text">Escuchar prueba</span>
          </button>
        </div>

        <div id="playback-widget" class="playback-widget" style="display: none;">
          <div class="playback-widget-row">
            <button id="widget-play-toggle" class="widget-play-btn" type="button" aria-label="Reproducir o pausar">
              <span class="widget-icon">▶</span>
            </button>
            <div class="widget-seek-container">
              <input type="range" id="widget-seek-bar" class="widget-seek-bar" value="0" min="0" max="100" step="0.1" aria-label="Progreso del audio" />
              <div class="widget-time-display">
                <span id="widget-current-time">00:00</span> / <span id="widget-total-time">00:00</span>
              </div>
            </div>
            <div class="widget-volume-container">
              <span class="volume-icon">🔊</span>
              <input type="range" id="widget-volume-bar" class="widget-volume-bar" value="100" min="0" max="100" aria-label="Volumen" />
            </div>
          </div>
        </div>

        <div class="status-msg-box">
          <span id="record-status" class="record-status">Iniciando micrófono...</span>
        </div>

        <audio id="preview-audio" preload="auto" playsinline style="display:none;"></audio>

        <a class="btn-back" href="${pageContext.request.contextPath}/terms" aria-label="Regresar a términos y condiciones">
          <img src="${pageContext.request.contextPath}/public/Prueba de Microfono/lets-icons-back-light.svg" alt="Back" />
          <span>Regresar</span>
        </a>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/prueba-microfono.js"></script>
  </body>
</html>
