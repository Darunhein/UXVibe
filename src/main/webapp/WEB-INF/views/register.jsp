<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <title>Crear Cuenta UX Vibe</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/registro.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>
  <body>
    <div class="registro-del-evaluador">
      <h1 class="registro-title">Crear Cuenta</h1>
      <p class="registro-subtitle">Completa los datos para crear tu cuenta</p>
      <% if (request.getAttribute("errorMessage") != null) { %>
        <p class="form-message form-message--error"><%= request.getAttribute("errorMessage") %></p>
      <% } %>
      <main class="frame">
        <form class="registro-form" action="${pageContext.request.contextPath}/register" method="post">
          <div class="registro-field">
            <label class="registro-label-name" for="nombre-completo">Nombre completo</label>
            <div class="registro-input-shell">
              <img class="registro-icon-panel" alt="" src="${pageContext.request.contextPath}/public/registro/merged-asset-4@2x.png" />
              <input id="nombre-completo" class="form-input registro-input" type="text" name="fullName" placeholder="Ingresa tu nombre completo" autocomplete="name" value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>" required />
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="email">Email</label>
            <div class="registro-input-shell">
              <img class="registro-icon-panel" alt="" src="${pageContext.request.contextPath}/public/registro/merged-asset-1@2x.png" />
              <input id="email" class="form-input registro-input" type="email" name="email" placeholder="Ingresa tu email" autocomplete="email" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required />
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="contrasena">Contraseña</label>
            <div class="registro-input-shell registro-input-shell--password">
              <img class="registro-icon-panel" alt="" src="${pageContext.request.contextPath}/public/registro/merged-asset-2@2x.png" />
              <input id="contrasena" class="form-input registro-input" type="password" name="password" placeholder="Crea una contraseña" autocomplete="new-password" minlength="8" required />
              <button
                class="registro-visibility"
                type="button"
                aria-label="Mostrar contraseña"
                aria-pressed="false"
                data-password-toggle
                data-target="#contrasena"
                data-field-label="contraseña"
              >
                <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt="" />
              </button>
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="confirmar-contrasena">Confirmar contraseña</label>
            <div class="registro-input-shell registro-input-shell--password">
              <img class="registro-icon-panel" alt="" src="${pageContext.request.contextPath}/public/registro/merged-asset-3@2x.png" />
              <input id="confirmar-contrasena" class="form-input registro-input" type="password" name="confirmPassword" placeholder="Confirma tu contraseña" autocomplete="new-password" minlength="8" required />
              <button
                class="registro-visibility"
                type="button"
                aria-label="Mostrar confirmación de contraseña"
                aria-pressed="false"
                data-password-toggle
                data-target="#confirmar-contrasena"
                data-field-label="confirmación de contraseña"
              >
                <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt="" />
              </button>
            </div>
          </div>

          <button class="registro-submit" type="submit">Crear cuenta</button>
          <a class="registro-link" href="${pageContext.request.contextPath}/login">Volver al inicio de sesión</a>
        </form>
      </main>
    </div>
    <script>
      document.querySelectorAll("[data-password-toggle]").forEach(function (toggleButton) {
        var inputSelector = toggleButton.getAttribute("data-target");
        var fieldLabel = toggleButton.getAttribute("data-field-label") || "contraseña";
        var input = inputSelector ? document.querySelector(inputSelector) : null;
        if (!input) {
          return;
        }

        toggleButton.addEventListener("click", function () {
          var isHidden = input.type === "password";
          input.type = isHidden ? "text" : "password";
          toggleButton.setAttribute("aria-pressed", String(isHidden));
          toggleButton.setAttribute("aria-label", (isHidden ? "Ocultar " : "Mostrar ") + fieldLabel);
        });
      });
    </script>
  </body>
</html>
