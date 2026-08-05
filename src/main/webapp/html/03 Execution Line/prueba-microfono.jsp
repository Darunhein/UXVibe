<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Prueba de Micrófono</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/prueba-microfono.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Actor:wght@400&display=swap"
    />
  </head>
  <body>
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
              <p class="status-value">Micrófono funcionando correctamente</p>
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
              <p class="status-value">00:08</p>
            </div>
          </div>

          <div class="status-item">
            <img
              class="status-icon"
              src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-3@2x.png"
              alt="Device icon"
            />
            <div class="status-text">
              <h3 class="status-label">Dispositivo detectado</h3>
              <p class="status-value">Microphone Array (Realtek® audio)</p>
            </div>
          </div>
        </div>

        <!-- Waveform Visualization -->
        <div class="waveform-container">
          <img
            class="waveform-image"
            src="${pageContext.request.contextPath}/public/Prueba de Microfono/merged-asset-4@2x.png"
            alt="Audio waveform"
          />
        </div>

        <!-- Back Button -->
        <button class="btn-back" aria-label="Regresar a la página anterior">
          <img
            src="${pageContext.request.contextPath}/public/Prueba de Microfono/lets-icons-back-light.svg"
            alt="Back icon"
          />
          <span>Regresar</span>
        </button>
      </main>
    </div>
  </body>
</html>

