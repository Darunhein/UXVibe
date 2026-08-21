<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.util.HtmlEscape" %>
<% Boolean validTokenObj=(Boolean) request.getAttribute("validToken"); boolean validToken=validTokenObj !=null &&
    validTokenObj; String token=HtmlEscape.text(request.getAttribute("token")); String email=HtmlEscape.text(request.getAttribute("email")); %>
    <!doctype html>
    <html lang="es">

    <head>
      <title>Restablecer Contraseña - UX Vibe</title>
      <meta charset="utf-8" />
      <meta name="viewport" content="initial-scale=1, width=device-width" />
      <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
      <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/recuperar-contrasena.css" />
      <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cambiar-contrasena.css" />
      <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
    </head>

    <body>
      <div class="recuperar-contrasena">
        <h1 class="bienvenido">Bienvenido</h1>
        <main class="frame">
          <section class="frame2">
            <div class="recuperar-contrasena-title">
              <h2>Restablecer contraseña</h2>
            </div>

            <% if (request.getAttribute("errorMessage") !=null) { %>
              <p class="form-message form-message--error">
                <%= HtmlEscape.text(request.getAttribute("errorMessage")) %>
              </p>
              <% } %>

                <% if (validToken) { %>
                  <div class="recuperar-contrasena-copy">
                    <p>
                      <% if (email !=null && !email.isBlank()) { %>
                        Crea una nueva contraseña para la cuenta <strong>
                          <%= email %>
                        </strong>.
                        <% } else { %>
                          Ingresa tu nueva contraseña para acceder a tu cuenta.
                          <% } %>
                    </p>
                  </div>

                  <form id="resetPasswordForm" action="${pageContext.request.contextPath}/reset-password" method="post">
                    <%@ include file="/WEB-INF/views/_csrf.jsp" %>
                    <input type="hidden" name="token" value="<%= token %>" />

                    <div class="reset-field-group">
                      <label class="field-label" for="reset-password">Nueva contraseña</label>
                      <div class="input-container">
                        <input id="reset-password" class="form-input text-field-input" type="password" name="password"
                          placeholder="Mínimo 8 caracteres" autocomplete="new-password" required />
                        <button id="toggle-new-password" class="password-visibility-btn" type="button"
                          aria-label="Mostrar u ocultar contraseña">
                          <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt="" />
                        </button>
                      </div>
                    </div>

                    <div class="reset-field-group">
                      <label class="field-label" for="reset-confirm-password">Confirmar nueva contraseña</label>
                      <div class="input-container">
                        <input id="reset-confirm-password" class="form-input text-field-input" type="password"
                          name="confirmPassword" placeholder="Repite tu nueva contraseña" autocomplete="new-password"
                          required />
                        <button id="toggle-confirm-password" class="password-visibility-btn" type="button"
                          aria-label="Mostrar u ocultar contraseña">
                          <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt="" />
                        </button>
                      </div>
                    </div>

                    <div id="password-match-error" class="password-feedback-error">
                      Las contraseñas no coinciden.
                    </div>

                    <button class="submit-btn" type="submit" id="submitResetBtn">
                      <span>Guardar nueva contraseña</span>
                    </button>
                  </form>
                  <% } else { %>
                    <div class="recuperar-contrasena-copy">
                      <p>Por seguridad, los enlaces de restablecimiento caducan después de 30 minutos o cuando ya han
                        sido utilizados.</p>
                    </div>
                    <div class="action-buttons-group">
                      <a class="submit-btn request-new-btn" href="${pageContext.request.contextPath}/recover">
                        <span>Solicitar un nuevo enlace</span>
                      </a>
                    </div>
                    <% } %>

                      <a class="back-btn" href="${pageContext.request.contextPath}/login">Volver al inicio de sesión</a>
          </section>
        </main>
      </div>
      <script src="${pageContext.request.contextPath}/JavaScript/cambiar-contrasena.js"></script>
    </body>

    </html>