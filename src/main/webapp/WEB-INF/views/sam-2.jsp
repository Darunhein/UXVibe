<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 2 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/sam-survey.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="satisfaction-survey-container">
      <main class="survey-frame">
        <h1 class="survey-title">Encuesta de Satisfacción SAM 2</h1>

        <section class="survey-question">
          <p>¿Qué tan impactante o estimulante fue tu experiencia?</p>
        </section>

        <div class="manikin-scale">
          <div class="manikin-item">
            <img src="${pageContext.request.contextPath}/public/SAM 2/image-23@2x.png" alt="Sin impacto"
              class="manikin" />
          </div>
          <div class="manikin-item">
            <img src="${pageContext.request.contextPath}/public/SAM 2/image-22@2x.png" alt="Leve" class="manikin" />
          </div>
          <div class="manikin-item">
            <img src="${pageContext.request.contextPath}/public/SAM 2/image-21@2x.png" alt="Moderado" class="manikin" />
          </div>
          <div class="manikin-item">
            <img src="${pageContext.request.contextPath}/public/SAM 2/image-20@2x.png" alt="Alto" class="manikin" />
          </div>
          <div class="manikin-item">
            <img src="${pageContext.request.contextPath}/public/SAM 2/image-24@2x.png" alt="Extremo" class="manikin" />
          </div>
        </div>

        <form class="survey-form" id="impactForm" action="${pageContext.request.contextPath}/sam-2" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
            <div class="scale-options">
              <% for (int i=1; i <=9; i++) { %>
                <label class="scale-option" for="rating<%= i %>">
                  <input type="radio" id="rating<%= i %>" name="impact" value="<%= i %>" required />
                  <span class="scale-number">
                    <%= i %>
                  </span>
                </label>
                <% } %>
            </div>

            <div class="survey-navigation">
              <a class="btn-back" id="samBackButton" href="${pageContext.request.contextPath}/sam-1"
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
    <script src="${pageContext.request.contextPath}/JavaScript/sam-2.js"></script>
  </body>

  </html>