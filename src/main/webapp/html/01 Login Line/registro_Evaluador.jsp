<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <title>Crear Cuenta</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/registro.css"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
    />
  </head>
  <body>
    <div class="registro-del-evaluador">
      <h1 class="registro-title">Crear Cuenta</h1>
      <p class="registro-subtitle">Completa los datos para crear tu cuenta</p>
      <main class="frame">
        <form class="registro-form">
          <div class="registro-field">
            <label class="registro-label-name" for="nombre-completo"
              >Nombre completo</label
            >
            <div class="registro-input-shell">
              <img
                class="registro-icon-panel"
                alt=""
                src="${pageContext.request.contextPath}/public/registro/merged-asset-4@2x.png"
              />
              <input
                id="nombre-completo"
                class="form-input registro-input"
                type="text"
                placeholder="Ingresa tu nombre completo"
                autocomplete="name"
              />
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="email">Email</label>
            <div class="registro-input-shell">
              <img
                class="registro-icon-panel"
                alt=""
                src="${pageContext.request.contextPath}/public/registro/merged-asset-1@2x.png"
              />
              <input
                id="email"
                class="form-input registro-input"
                type="email"
                placeholder="Ingresa tu email"
                autocomplete="email"
              />
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="contrasena">Contraseña</label>
            <div class="registro-input-shell registro-input-shell--password">
              <img
                class="registro-icon-panel"
                alt=""
                src="${pageContext.request.contextPath}/public/registro/merged-asset-2@2x.png"
              />
              <input
                id="contrasena"
                class="form-input registro-input"
                type="password"
                placeholder="Crea una contraseña"
                autocomplete="new-password"
              />
              <button
                class="registro-visibility"
                type="button"
                aria-label="Mostrar contraseña"
                aria-pressed="false"
                data-target="contrasena"
              >
                <img
                  src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png"
                  alt=""
                />
              </button>
            </div>
          </div>

          <div class="registro-field">
            <label class="registro-label" for="confirmar-contrasena"
              >Confirmar contraseña</label
            >
            <div class="registro-input-shell registro-input-shell--password">
              <img
                class="registro-icon-panel"
                alt=""
                src="${pageContext.request.contextPath}/public/registro/merged-asset-3@2x.png"
              />
              <input
                id="confirmar-contrasena"
                class="form-input registro-input"
                type="password"
                placeholder="Confirma tu contraseña"
                autocomplete="new-password"
              />
              <button
                class="registro-visibility"
                type="button"
                aria-label="Mostrar confirmación de contraseña"
                aria-pressed="false"
                data-target="confirmar-contrasena"
              >
                <img
                  src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png"
                  alt=""
                />
              </button>
            </div>
          </div>

          <button class="registro-submit" type="submit">Crear cuenta</button>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/text-only-validation.js"></script>
  </body>
</html>
