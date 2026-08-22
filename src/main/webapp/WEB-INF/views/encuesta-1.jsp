<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Encuesta de Usabilidad - Parte 1 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/encuesta-survey.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="survey-container">
      <main class="survey-frame">
        <section class="survey-header">
          <div class="header-left">
            <h1 class="survey-title">Encuesta de Satisfacción y Usabilidad</h1>
            <p class="survey-instructions">
              Evalúa tu experiencia seleccionando la opción que mejor represente tu opinión.
            </p>
          </div>
          <div class="scale-legend">
            <p>1 = Totalmente en desacuerdo</p>
            <p>2 = En desacuerdo</p>
            <p>3 = Neutral</p>
            <p>4 = De acuerdo</p>
            <p>5 = Totalmente de acuerdo</p>
          </div>
        </section>

        <form class="survey-form" id="surveyForm1" name="satisfaction"
          action="${pageContext.request.contextPath}/encuesta-1" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
          <div class="questions-container">
            <% String[] qTexts1={ "Me resultó fácil aprender a navegar por esta página/sistema web."
              , "La estructura del menú y los enlaces es intuitiva y sé dónde encontrar la información."
              , "Las funciones y herramientas de la página cubren completamente mis necesidades."
              , "Este sistema web me permite realizar mis tareas de forma más rápida y eficiente."
              , "Creo que el sistema web es innecesariamente complejo o difícil de entender." }; for (int q=1; q <=5;
              q++) { %>
              <div class="question-row">
                <div class="question-text">
                  <span class="question-number">
                    <%= q %>.-
                  </span>
                  <span>
                    <%= qTexts1[q - 1] %>
                  </span>
                </div>
                <div class="scale-options">
                  <% for (int opt=1; opt <=5; opt++) { %>
                    <label class="scale-option">
                      <input type="radio" name="q<%= q %>" value="<%= opt %>" required aria-label="Opción <%= opt %>" />
                      <span class="scale-dot"></span>
                      <span class="scale-number">
                        <%= opt %>
                      </span>
                    </label>
                    <% } %>
                </div>
              </div>
              <% } %>
          </div>

          <div class="survey-navigation">
            <a class="btn-back" id="surveyBackButton" href="${pageContext.request.contextPath}/sam-3"
              aria-label="Regresar">
              <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />
              <span>Regresar</span>
            </a>
            <button type="submit" id="surveyNextButton" class="btn-next" aria-label="Siguiente">
              <span>Siguiente</span>
              <img src="${pageContext.request.contextPath}/public/encuesta 1/carbon-next-outline.svg" alt=""
                aria-hidden="true" />
            </button>
          </div>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/encuesta-1.js"></script>
    <script src="${pageContext.request.contextPath}/JavaScript/survey-autoclose.js"></script>
  </body>

  </html>