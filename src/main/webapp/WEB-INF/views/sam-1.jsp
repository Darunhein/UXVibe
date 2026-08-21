<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 1 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/sam-survey.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="satisfaction-survey-container">
      <main class="survey-frame">
        <h1 class="survey-title">Encuesta de Satisfacción (SAM 1)</h1>

        <section class="survey-question">
          <p>¿Cómo te sientes después de haber interactuado con la página/sistema Web?</p>
        </section>

        <div class="emoji-scale">
          <div class="emoji-item">
            <img src="${pageContext.request.contextPath}/public/SAM 1/image-19@2x.png" alt="Muy satisfecho"
              class="emoji" />
          </div>
          <div class="emoji-item">
            <img src="${pageContext.request.contextPath}/public/SAM 1/image-18@2x.png" alt="Satisfecho" class="emoji" />
          </div>
          <div class="emoji-item">
            <img src="${pageContext.request.contextPath}/public/SAM 1/image-17@2x.png" alt="Neutral" class="emoji" />
          </div>
          <div class="emoji-item">
            <img src="${pageContext.request.contextPath}/public/SAM 1/image-16@2x.png" alt="Insatisfecho"
              class="emoji" />
          </div>
          <div class="emoji-item">
            <img src="${pageContext.request.contextPath}/public/SAM 1/image-15@2x.png" alt="Muy insatisfecho"
              class="emoji" />
          </div>
        </div>

        <form class="survey-form" id="satisfactionForm" action="${pageContext.request.contextPath}/sam-1" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
          <div class="scale-options">
            <% for (int i=1; i <=9; i++) { %>
              <label class="scale-option" for="rating<%= i %>">
                <input type="radio" id="rating<%= i %>" name="satisfaction" value="<%= i %>" required />
                <span class="scale-number">
                  <%= i %>
                </span>
              </label>
              <% } %>
          </div>

          <div class="survey-navigation">
            <a class="btn-back" id="samBackButton" href="${pageContext.request.contextPath}/cuestionario-sb-2"
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
    <script src="${pageContext.request.contextPath}/JavaScript/sam-1.js"></script>
  </body>

  </html>