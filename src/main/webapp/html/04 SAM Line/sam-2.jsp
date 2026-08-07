<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 2</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/sam-survey.css"
    />
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
          <p>¿Qué tan impactante fue tu experiencia?</p>
        </section>

        <!-- Manikin Scale Display -->
        <div class="manikin-scale">
          <div class="manikin-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/image-20@2x.png"
              alt="Not impactful"
              class="manikin"
            />
          </div>
          <div class="manikin-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/image-21@2x.png"
              alt="Slightly impactful"
              class="manikin"
            />
          </div>
          <div class="manikin-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/image-22@2x.png"
              alt="Moderately impactful"
              class="manikin"
            />
          </div>
          <div class="manikin-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/image-23@2x.png"
              alt="Very impactful"
              class="manikin"
            />
          </div>
          <div class="manikin-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/image-24@2x.png"
              alt="Extremely impactful"
              class="manikin"
            />
          </div>
        </div>

        <!-- Scale Radio Options (1-9) -->
        <form
          class="survey-form"
          id="impactForm"
          action="${pageContext.request.contextPath}/html/04%20SAM%20Line/sam-3.jsp"
          method="get"
        >
          <div class="scale-options">
            <label class="scale-option" for="rating1">
              <input
                type="radio"
                id="rating1"
                name="impact"
                value="1"
                required
              />
              <span class="scale-number">1</span>
            </label>
            <label class="scale-option" for="rating2">
              <input
                type="radio"
                id="rating2"
                name="impact"
                value="2"
                required
              />
              <span class="scale-number">2</span>
            </label>
            <label class="scale-option" for="rating3">
              <input
                type="radio"
                id="rating3"
                name="impact"
                value="3"
                required
              />
              <span class="scale-number">3</span>
            </label>
            <label class="scale-option" for="rating4">
              <input
                type="radio"
                id="rating4"
                name="impact"
                value="4"
                required
              />
              <span class="scale-number">4</span>
            </label>
            <label class="scale-option" for="rating5">
              <input
                type="radio"
                id="rating5"
                name="impact"
                value="5"
                required
              />
              <span class="scale-number">5</span>
            </label>
            <label class="scale-option" for="rating6">
              <input
                type="radio"
                id="rating6"
                name="impact"
                value="6"
                required
              />
              <span class="scale-number">6</span>
            </label>
            <label class="scale-option" for="rating7">
              <input
                type="radio"
                id="rating7"
                name="impact"
                value="7"
                required
              />
              <span class="scale-number">7</span>
            </label>
            <label class="scale-option" for="rating8">
              <input
                type="radio"
                id="rating8"
                name="impact"
                value="8"
                required
              />
              <span class="scale-number">8</span>
            </label>
            <label class="scale-option" for="rating9">
              <input
                type="radio"
                id="rating9"
                name="impact"
                value="9"
                required
              />
              <span class="scale-number">9</span>
            </label>
          </div>

          <!-- Next Button -->
          <button
            type="button"
            id="samNextButton"
            class="btn-next"
            aria-label="Siguiente"
            onclick="
              var rating = document.querySelector(
                'input[name=&quot;impact&quot;]:checked',
              );
              if (!rating) {
                alert('Por favor, selecciona una valoración');
                return;
              }
              window.location.href =
                '${pageContext.request.contextPath}/html/04%20SAM%20Line/sam-3.jsp';
            "
          >
            <span>Siguiente</span>
            <img
              src="${pageContext.request.contextPath}/public/SAM 2/carbon-next-outline.svg"
              alt="Next icon"
            />
          </button>
        </form>
        <a
          class="btn-back"
          href="#"
          id="samBackButton"
          aria-label="Regresar a la página anterior"
        >
          <img
            src="${pageContext.request.contextPath}/public/SAM 2/lets-icons-back-light.svg"
            alt=""
            aria-hidden="true"
          />
          <span>Regresar</span>
        </a>
      </main>
    </div>

    <script src="${pageContext.request.contextPath}/JavaScript/sam-2.js"></script>
  </body>
</html>
