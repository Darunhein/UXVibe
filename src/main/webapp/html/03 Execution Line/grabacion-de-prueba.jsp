<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Grabación de prueba</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/grabacion-de-prueba.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Advent+Pro:wght@400&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Actor:wght@400&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=ADLaM+Display:wght@400&display=swap"
    />
  </head>
  <body>
    <div class="pantalla-escondida-de-prueba">
      <main class="frame">
        <h1 class="punto-de-prueba">Punto de Prueba</h1>
        <section class="frame2" aria-label="Panel de grabación de prueba">
          <div class="wave-card" aria-hidden="true">
            <div class="wave-card-inner">
              <span class="wave-wave wave-wave-a"></span>
              <span class="wave-wave wave-wave-b"></span>
              <span class="wave-wave wave-wave-c"></span>
              <span class="wave-wave wave-wave-d"></span>
              <span class="wave-wave wave-wave-e"></span>
              <span class="wave-wave wave-wave-f"></span>
              <span class="wave-wave wave-wave-g"></span>
              <span class="wave-wave wave-wave-h"></span>
            </div>
          </div>

          <section class="playback-bar" aria-label="Barra de tiempo">
            <button class="play-button" type="button" aria-label="Reproducir">
              <span class="play-button-icon" aria-hidden="true"></span>
            </button>

            <div class="playback-line"></div>

            <h2 class="h2">00:00</h2>
          </section>

          <div class="pause-stack">
            <button
              class="material-symbolspause-icon"
              type="button"
              aria-label="Pausar grabación"
            >
              <span class="pause-button-icon" aria-hidden="true"></span>
            </button>
          </div>
        </section>

        <button class="regresar" type="button" aria-label="Regresar">
          <img
            class="lets-iconsback-light"
            src="${pageContext.request.contextPath}/public/Grabacion de prueba/lets-icons-back-light.svg"
            alt=""
          />
          <div class="regresar2">Regresar</div>
        </button>
      </main>

      <img
        class="merged-asset-1-icon"
        loading="lazy"
        alt=""
        src="${pageContext.request.contextPath}/public/Grabacion de prueba/merged-asset-1@2x.png"
      />

      <img
        class="merged-asset-2-icon"
        loading="lazy"
        alt=""
        src="${pageContext.request.contextPath}/public/Grabacion de prueba/merged-asset-2.svg"
      />
    </div>

    <script>
      const pauseButton = document.querySelector(".material-symbolspause-icon");
      if (pauseButton) {
        pauseButton.addEventListener("click", () => {
          pauseButton.classList.toggle("is-paused");
        });
      }
    </script>
  </body>
</html>

