<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="mx.edu.utez.uxvibe.util.HtmlEscape" %>
<% String emailVal = HtmlEscape.text(request.getAttribute("email")); %>
  <!doctype html>
  <html lang="es">

  <head>
    <title>Recuperar Contraseña - UX Vibe</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/recuperar-contrasena.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" />
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
            <p>Ingresa tu email y te enviaremos un enlace seguro para restablecer tu contraseña.</p>
          </div>

          <% if (request.getAttribute("errorMessage") !=null) { %>
            <p class="form-message form-message--error">
              <%= HtmlEscape.text(request.getAttribute("errorMessage")) %>
            </p>
            <% } %>

              <% if (request.getAttribute("successMessage") !=null) { %>
                <p class="form-message form-message--success">
                  <%= HtmlEscape.text(request.getAttribute("successMessage")) %>
                </p>
                <% } %>

                  <form id="recoverForm" action="${pageContext.request.contextPath}/recover" method="post">
                    <%@ include file="/WEB-INF/views/_csrf.jsp" %>
                    <label class="email-label" for="recuperar-email">Email</label>
                    <section class="email-row">
                      <div class="email-shell">
                        <img class="email-icon-panel" alt=""
                          src="${pageContext.request.contextPath}/public/recuperar-contrasena/merged-asset-1@2x.png" />
                        <input id="recuperar-email" class="form-input email-input" placeholder="Ingresa tu email"
                          type="email" name="email" autocomplete="email" value="<%= emailVal %>" required />
                      </div>
                    </section>

                    <button class="submit-btn" type="submit">
                      <img class="submit-btn-icon"
                        src="${pageContext.request.contextPath}/public/recuperar-contrasena/material-symbols-light-link.svg"
                        alt="" />
                      <span>Enviar enlace</span>
                    </button>
                  </form>

                  <a class="back-btn" href="${pageContext.request.contextPath}/login">Volver al inicio de sesión</a>
        </section>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/recuperar-contrasena.js"></script>
  </body>

  </html>