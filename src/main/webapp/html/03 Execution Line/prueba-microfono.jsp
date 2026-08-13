<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Prueba de Micrófono</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/prueba-microfono.css"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Actor:wght@400&display=swap"
    />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="microphone-test-container">
      <main class="microphone-test-frame">
        <!-- Header Section -->
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
            <h2 class="test-subtitle">Habla al micrófono</h2>
          </div>
        </section>

        <!-- Status Information -->
        <div class="status-section">
          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-1@2x.png"
              alt="Status icon"
            />
            <div class="status-text">
              <h3 class="status-label">Estado</h3>
              <p class="status-value">Micrófono sin iniciar</p>
            </div>
          </div>

          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-2@2x.png"
              alt="Duration icon"
            />
            <div class="status-text">
              <h3 class="status-label">Duración de la prueba</h3>
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
              <p class="status-value" id="device-name">No detectado</p>
            </div>
          </div>
        </div>

        <div class="waveform-container" aria-live="polite">
          <!-- waveform bars inserted by JS -->
        </div>

        <div class="microphone-controls">
          <!-- Simplified controls: single record toggle and play -->
          <button id="btn-record" class="btn-primary">Grabar</button>
          <button id="btn-play" class="btn-secondary" disabled>Reproducir</button>
          <span id="record-status" class="record-status">Sin grabación</span>
        </div>

        <!-- Hidden audio element for playback -->
        <audio id="preview-audio" controls style="display:none"></audio>

        <!-- Back Button -->
        <a
          class="btn-back"
          href="${pageContext.request.contextPath}/html/03%20Execution%20Line/terminos-y-condiciones.jsp"
          aria-label="Regresar a la página anterior"
        >
          <img
            src="${pageContext.request.contextPath}/public/Prueba de Microfono/lets-icons-back-light.svg"
            alt="Back icon"
          />
          <span>Regresar</span>
        </a>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/prueba-microfono.js"></script>
  </body>
</html>
