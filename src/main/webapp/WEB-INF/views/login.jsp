<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <% String emailVal="" ; Object emailAttr=request.getAttribute("email"); if (emailAttr !=null) {
    emailVal=String.valueOf(emailAttr).replace("&", "&amp;" ).replace("\"", "&quot;").replace("<", "&lt;"); } %>
  <!doctype html>
  <html lang="es">

  <head>
    <title>Iniciar Sesión - UX Vibe</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/login.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>

  <body>
    <form class="login-del-evaluador" id="loginForm" action="${pageContext.request.contextPath}/login" method="post">
      <%@ include file="/WEB-INF/views/_csrf.jsp" %>
      <h1 class="bienvenido">Bienvenido</h1>
      <% if (request.getAttribute("errorMessage") !=null) { %>
        <p class="form-message form-message--error">
          <%= request.getAttribute("errorMessage") %>
        </p>
        <% } %>
          <% if (request.getAttribute("successMessage") !=null) { %>
            <p class="form-message form-message--success">
              <%= request.getAttribute("successMessage") %>
            </p>
            <% } %>
              <main class="frame">
                <div class="frame2">
                  <div class="merged-field merged-field-email">
                    <div class="field-label-group">
                      <label class="email" for="login-email">Email</label>
                    </div>
                    <div class="merged-image-wrapper">
                      <img class="merged-asset-1-icon" alt=""
                        src="${pageContext.request.contextPath}/public/login/merged-asset-1@2x.png" />
                      <div class="email-field-group">
                        <input id="login-email" class="form-input ingresa-tu-email" type="email" name="email"
                          placeholder="Ingresa tu email" autocomplete="email" value="<%= emailVal %>" required />
                      </div>
                    </div>
                  </div>
                  <div class="merged-field merged-field-password">
                    <div class="field-label-group">
                      <label class="contrasea" for="login-password">Contraseña</label>
                    </div>
                    <div class="merged-image-wrapper">
                      <img class="merged-asset-2-icon" alt=""
                        src="${pageContext.request.contextPath}/public/login/merged-asset-2@2x.png" />
                      <div class="password-field-group">
                        <input id="login-password" class="form-input ingresa-tu-contraseña" type="password"
                          name="password" placeholder="Ingresa tu contraseña" autocomplete="current-password"
                          required />
                        <button id="login-password-toggle" class="login-password-visibility" type="button"
                          aria-label="Mostrar contraseña" aria-pressed="false">
                          <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt="" />
                        </button>
                      </div>
                    </div>
                  </div>
                  <section class="actions-group">
                    <div class="actions-group2">
                      <button class="entrar" type="submit">
                        <span class="entrar-text">Entrar</span>
                      </button>
                      <a class="crear-cuenta" href="${pageContext.request.contextPath}/register">Crear cuenta</a>
                      <a class="olvid-mi-contrasea" href="${pageContext.request.contextPath}/recover">Olvidé mi
                        contraseña</a>
                    </div>
                  </section>
                </div>
              </main>
    </form>
    <script src="${pageContext.request.contextPath}/JavaScript/login.js"></script>
  </body>

  </html>