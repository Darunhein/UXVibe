<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <title>crear prueba</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/crear-prueba.css"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;800&display=swap"
    />
  </head>
  <body>
    <div class="crear-prueba">
      <main class="crear-prueba__shell">
        <section
          class="crear-prueba__card"
          aria-labelledby="crear-prueba-title"
        >
          <div class="crear-prueba__top-line"></div>

          <img
            class="crear-prueba__hero"
            src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-1@2x.png"
            alt="Crear prueba"
          />

          <h1 id="crear-prueba-title" class="crear-prueba__title">
            Crear prueba nueva
          </h1>
          <p class="crear-prueba__subtitle">
            Ingresa el nombre de la prueba que deseas crear
          </p>

          <label class="visually-hidden" for="nombre-prueba"
            >Nombre de la prueba</label
          >
          <div class="crear-prueba__input-shell">
            <img
              class="crear-prueba__input-icon"
              src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-2@2x.png"
              alt=""
            />
            <input
              id="nombre-prueba"
              class="crear-prueba__input"
              type="text"
              placeholder="Nombre de la prueba"
              autocomplete="off"
            />
          </div>

          <div class="crear-prueba__actions">
            <button class="crear-prueba__primary" type="button">
              <img
                class="crear-prueba__action-icon"
                src="${pageContext.request.contextPath}/public/crear-prueba/material-symbols-light-new-window-rounded.svg"
                alt=""
              />
              <span>Crear Prueba</span>
            </button>

            <button class="crear-prueba__secondary" type="button">
              <img
                class="crear-prueba__action-icon"
                src="${pageContext.request.contextPath}/public/crear-prueba/ep-back.svg"
                alt=""
              />
              <span>Cancelar</span>
            </button>
          </div>

          <div class="crear-prueba__bottom-line"></div>
        </section>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/text-only-validation.js"></script>
  </body>
</html>
