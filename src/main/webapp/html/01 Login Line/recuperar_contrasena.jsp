<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <title>recuperar contraseña</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/recuperar-contrasena.css"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
    />
  </head>
  <body>
    <div class="recuperar-contrasena">
      <h1 class="bienvenido">Bienvenido</h1>
      <main class="frame">
        <section class="frame2">
          <div class="recuperar-contrasena-title">
            <h2>Recuperar contraseña</h2>
          </div>

          <div class="recuperar-contrasena-copy">
            <p>
              Ingresa tu email y enviamos un enlace para restablecer tu
              contraseña.
            </p>
          </div>

          <section class="notice" aria-live="polite">
            <img
              class="notice-icon"
              src="${pageContext.request.contextPath}/public/recuperar-contrasena/lets-icons-check-fill@2x.png"
              alt=""
            />
            <div class="notice-copy">
              <h3>¡Email enviado!</h3>
              <p>
                Revisa tu bandeja de entrada y sigue las instrucciones para
                restablecer tu contraseña.
              </p>
            </div>
          </section>

          <form
            action="${pageContext.request.contextPath}/recover"
            method="post"
            class="email-row"
          >
            <label class="email-label" for="recuperar-email">Email</label>

            <div class="email-shell">
              <img
                class="email-icon-panel"
                alt=""
                src="${pageContext.request.contextPath}/public/recuperar-contrasena/merged-asset-1@2x.png"
              />
              <input
                id="recuperar-email"
                name="email"
                class="form-input email-input"
                placeholder="Ingresa tu email"
                type="email"
                autocomplete="email"
                required
              />
            </div>

            <button class="submit-btn" type="submit">
              <img
                class="submit-btn-icon"
                src="${pageContext.request.contextPath}/public/recuperar-contrasena/material-symbols-light-link.svg"
                alt=""
              />
              <span>Enviar enlace</span>
            </button>
          </form>

          <a class="back-btn" href="${pageContext.request.contextPath}/login">
            Volver al inicio de sesión
          </a>
        </section>
      </main>
    </div>
  </body>
</html>
