<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Encuesta de Usabilidad - Parte 3 - UX Vibe</title>
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
              Evalúa las siguientes afirmaciones finales sobre tu percepción global de la plataforma.
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

        <form class="survey-form" id="surveyForm3" name="satisfaction3" action="${pageContext.request.contextPath}/encuesta-3" method="post">
          <div class="questions-container">
            <%
              String[] qTexts3 = {
                "El sistema funciona correctamente en mi navegador y no experimenté errores técnicos.",
                "La plataforma se adapta bien y es fácil de usar si accedo desde el teléfono móvil.",
                "Me siento satisfecho con mi experiencia general utilizando este sitio/sistema web.",
                "Recomendaría este sitio o sistema web a otros colegas o usuarios.",
                "Si tuviera otra alternativa que haga lo mismo, preferiría no usar esta web."
              };
              for (int q = 11; q <= 15; q++) {
            %>
              <div class="question-row">
                <div class="question-text">
                  <span class="question-number"><%= q %>.-</span>
                  <span><%= qTexts3[q - 11] %></span>
                </div>
                <div class="scale-options">
                  <% for (int opt = 1; opt <= 5; opt++) { %>
                    <label class="scale-option">
                      <input type="radio" name="q<%= q %>" value="<%= opt %>" required aria-label="Opción <%= opt %>" />
                      <span class="scale-dot"></span>
                      <span class="scale-number"><%= opt %></span>
                    </label>
                  <% } %>
                </div>
              </div>
            <% } %>
          </div>

          <div class="survey-navigation">
            <a class="btn-back" id="surveyBackButton" href="${pageContext.request.contextPath}/encuesta-2" aria-label="Regresar">
              <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />
              <span>Regresar</span>
            </a>
            <button type="submit" id="surveyNextButton" class="btn-next" aria-label="Finalizar encuesta">
              <span>Finalizar</span>
              <img src="${pageContext.request.contextPath}/public/encuesta 3/carbon-next-outline.svg" alt="" aria-hidden="true" />
            </button>
          </div>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/encuesta-3.js"></script>
  </body>
</html>
