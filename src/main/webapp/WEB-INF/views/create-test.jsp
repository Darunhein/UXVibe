<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <title>Crear prueba</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/crear-prueba.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;800&display=swap" />
  </head>
  <body>
    <div class="crear-prueba">
      <main class="crear-prueba__shell">
        <section class="crear-prueba__card" aria-labelledby="crear-prueba-title">
          <div class="crear-prueba__top-line"></div>

          <img class="crear-prueba__hero" src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-1@2x.png" alt="Crear prueba" />

          <h1 id="crear-prueba-title" class="crear-prueba__title">Crear prueba nueva</h1>
          <p class="crear-prueba__subtitle">Ingresa el nombre de la prueba que deseas crear</p>

          <% if (request.getAttribute("errorMessage") != null) { %>
            <p class="form-message form-message--error"><%= request.getAttribute("errorMessage") %></p>
          <% } %>

          <form class="crear-prueba__form" action="${pageContext.request.contextPath}/create-test" method="post">
            <label class="visually-hidden" for="nombre-prueba">Nombre de la prueba</label>
            <div class="crear-prueba__input-shell">
              <img class="crear-prueba__input-icon" src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-2@2x.png" alt="" />
              <input id="nombre-prueba" class="form-input crear-prueba__input" type="text" name="testName" placeholder="Nombre de la prueba" autocomplete="off" value="<%= request.getAttribute("testName") != null ? request.getAttribute("testName") : "" %>" required />
            </div>

            <label class="visually-hidden" for="descripcion-prueba">Descripción de la prueba</label>
            <div class="crear-prueba__input-shell crear-prueba__input-shell--textarea">
              <img class="crear-prueba__input-icon" src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-3@2x.png" alt="" />
              <textarea id="descripcion-prueba" class="form-input crear-prueba__input crear-prueba__textarea" name="description" placeholder="Descripción de la prueba" rows="4"><%= request.getAttribute("description") != null ? request.getAttribute("description") : "" %></textarea>
            </div>

            <label class="visually-hidden" for="sistema-prueba">Sistema a evaluar</label>
            <div class="crear-prueba__input-shell">
              <img class="crear-prueba__input-icon" src="${pageContext.request.contextPath}/public/crear-prueba/merged-asset-4@2x.png" alt="" />
              <input id="sistema-prueba" class="form-input crear-prueba__input" type="text" name="systemLink" placeholder="Enlace del sistema que se probará" autocomplete="off" value="<%= request.getAttribute("systemLink") != null ? request.getAttribute("systemLink") : "" %>" />
            </div>

            <div class="crear-prueba__actions">
              <button class="crear-prueba__primary" type="submit">
                <img class="crear-prueba__action-icon" src="${pageContext.request.contextPath}/public/crear-prueba/material-symbols-light-new-window-rounded.svg" alt="" />
                <span>Crear Prueba</span>
              </button>

              <a class="crear-prueba__secondary" href="${pageContext.request.contextPath}/tests">
                <img class="crear-prueba__action-icon" src="${pageContext.request.contextPath}/public/crear-prueba/ep-back.svg" alt="" />
                <span>Cancelar</span>
              </a>
            </div>
          </form>

          <div class="crear-prueba__bottom-line"></div>
        </section>
      </main>
    </div>
  </body>
</html>
