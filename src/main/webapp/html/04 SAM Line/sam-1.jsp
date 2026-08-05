<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/sam-survey.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;700&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Afacad:wght@400&display=swap"
    />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="satisfaction-survey-container">
      <main class="survey-frame">
        <!-- Title -->
        <h1 class="survey-title">Encuesta de Satisfacción</h1>

        <!-- Question -->
        <section class="survey-question">
          <p>
            ¿Cómo te sientes después de haber interactuado con la página/sistema
            Web?
          </p>
        </section>

        <!-- Emoji Scale Display -->
        <div class="emoji-scale">
          <div class="emoji-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/image-19@2x.png"
              alt="Very satisfied"
              class="emoji"
            />
          </div>
          <div class="emoji-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/image-18@2x.png"
              alt="Satisfied"
              class="emoji"
            />
          </div>
          <div class="emoji-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/image-17@2x.png"
              alt="Neutral"
              class="emoji"
            />
          </div>
          <div class="emoji-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/image-16@2x.png"
              alt="Unsatisfied"
              class="emoji"
            />
          </div>
          <div class="emoji-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/image-15@2x.png"
              alt="Very unsatisfied"
              class="emoji"
            />
          </div>
        </div>

        <!-- Scale Radio Options (1-9) -->
        <form class="survey-form" id="satisfactionForm" action="${pageContext.request.contextPath}/html/04%20SAM%20Line/sam-2.jsp" method="get">
          <div class="scale-options">
            <label class="scale-option" for="rating1">
              <input
                type="radio"
                id="rating1"
                name="satisfaction"
                value="1"
                required
              />
              <span class="scale-number">1</span>
            </label>
            <label class="scale-option" for="rating2">
              <input
                type="radio"
                id="rating2"
                name="satisfaction"
                value="2"
                required
              />
              <span class="scale-number">2</span>
            </label>
            <label class="scale-option" for="rating3">
              <input
                type="radio"
                id="rating3"
                name="satisfaction"
                value="3"
                required
              />
              <span class="scale-number">3</span>
            </label>
            <label class="scale-option" for="rating4">
              <input
                type="radio"
                id="rating4"
                name="satisfaction"
                value="4"
                required
              />
              <span class="scale-number">4</span>
            </label>
            <label class="scale-option" for="rating5">
              <input
                type="radio"
                id="rating5"
                name="satisfaction"
                value="5"
                required
              />
              <span class="scale-number">5</span>
            </label>
            <label class="scale-option" for="rating6">
              <input
                type="radio"
                id="rating6"
                name="satisfaction"
                value="6"
                required
              />
              <span class="scale-number">6</span>
            </label>
            <label class="scale-option" for="rating7">
              <input
                type="radio"
                id="rating7"
                name="satisfaction"
                value="7"
                required
              />
              <span class="scale-number">7</span>
            </label>
            <label class="scale-option" for="rating8">
              <input
                type="radio"
                id="rating8"
                name="satisfaction"
                value="8"
                required
              />
              <span class="scale-number">8</span>
            </label>
            <label class="scale-option" for="rating9">
              <input
                type="radio"
                id="rating9"
                name="satisfaction"
                value="9"
                required
              />
              <span class="scale-number">9</span>
            </label>
          </div>

          <!-- Next Button -->
          <button type="submit" class="btn-next" aria-label="Siguiente">
            <span>Siguiente</span>
            <img
              src="${pageContext.request.contextPath}/public/SAM 1/carbon-next-outline.svg"
              alt="Next icon"
            />
          </button>
        </form>
      </main>
    </div>

  </body>
</html>
