<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 3 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/sam-survey.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="satisfaction-survey-container">
      <main class="survey-frame">
        <h1 class="survey-title">Encuesta de Satisfacción (SAM 3)</h1>

        <section class="survey-question">
          <p>¿Qué tanto dominio o control sentiste tener sobre tus emociones y acciones?</p>
        </section>

        <div class="character-scale">
          <div class="character-item">
            <img src="${pageContext.request.contextPath}/public/SAM 3/image-25@2x.png" alt="Sin control"
              class="character" />
          </div>
          <div class="character-item">
            <img src="${pageContext.request.contextPath}/public/SAM 3/image-26@2x.png" alt="Bajo control"
              class="character" />
          </div>
          <div class="character-item">
            <img src="${pageContext.request.contextPath}/public/SAM 3/image-29@2x.png" alt="Moderado"
              class="character" />
          </div>
          <div class="character-item">
            <img src="${pageContext.request.contextPath}/public/SAM 3/image-28@2x.png" alt="Buen control"
              class="character" />
          </div>
          <div class="character-item">
            <img src="${pageContext.request.contextPath}/public/SAM 3/image-27@2x.png" alt="Total control"
              class="character" />
          </div>
        </div>

        <form class="survey-form" id="controlForm" action="${pageContext.request.contextPath}/sam-3" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
          <div class="scale-options">
            <% for (int i=1; i <=9; i++) { %>
              <label class="scale-option" for="rating<%= i %>">
                <input type="radio" id="rating<%= i %>" name="control" value="<%= i %>" required />
                <span class="scale-number">
                  <%= i %>
                </span>
              </label>
              <% } %>
          </div>

          <div class="survey-navigation">
            <a class="btn-back" id="samBackButton" href="${pageContext.request.contextPath}/sam-2"
              aria-label="Regresar">
              <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />
              <span>Regresar</span>
            </a>
            <button type="submit" id="samNextButton" class="btn-next" aria-label="Siguiente">
              <span>Siguiente</span>
              <img src="${pageContext.request.contextPath}/public/SAM 1/carbon-next-outline.svg" alt="Next icon" />
            </button>
          </div>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/sam-3.js"></script>
  </body>

  </html>