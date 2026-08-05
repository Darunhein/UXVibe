<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Encuesta de Satisfacción - SAM 3</title>
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
  <body>
    <div class="satisfaction-survey-container">
      <main class="survey-frame">
        <!-- Title -->
        <h1 class="survey-title">Encuesta de Satisfacción</h1>

        <!-- Question -->
        <section class="survey-question">
          <p>¿Qué tanto dominio tuviste sobre tus emociones y sentimientos?</p>
        </section>

        <!-- Character/Figure Scale Display -->
        <div class="character-scale">
          <div class="character-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 3/image-25@2x.png"
              alt="No control over emotions"
              class="character"
            />
          </div>
          <div class="character-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 3/image-26@2x.png"
              alt="Low control over emotions"
              class="character"
            />
          </div>
          <div class="character-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 3/image-29@2x.png"
              alt="Moderate control over emotions"
              class="character"
            />
          </div>
          <div class="character-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 3/image-28@2x.png"
              alt="Good control over emotions"
              class="character"
            />
          </div>
          <div class="character-item">
            <img
              src="${pageContext.request.contextPath}/public/SAM 3/image-27@2x.png"
              alt="Full control over emotions"
              class="character"
            />
          </div>
        </div>

        <!-- Scale Radio Options (1-9) -->
        <form class="survey-form" id="controlForm">
          <div class="scale-options">
            <label class="scale-option" for="rating1">
              <input
                type="radio"
                id="rating1"
                name="control"
                value="1"
                required
              />
              <span class="scale-number">1</span>
            </label>
            <label class="scale-option" for="rating2">
              <input
                type="radio"
                id="rating2"
                name="control"
                value="2"
                required
              />
              <span class="scale-number">2</span>
            </label>
            <label class="scale-option" for="rating3">
              <input
                type="radio"
                id="rating3"
                name="control"
                value="3"
                required
              />
              <span class="scale-number">3</span>
            </label>
            <label class="scale-option" for="rating4">
              <input
                type="radio"
                id="rating4"
                name="control"
                value="4"
                required
              />
              <span class="scale-number">4</span>
            </label>
            <label class="scale-option" for="rating5">
              <input
                type="radio"
                id="rating5"
                name="control"
                value="5"
                required
              />
              <span class="scale-number">5</span>
            </label>
            <label class="scale-option" for="rating6">
              <input
                type="radio"
                id="rating6"
                name="control"
                value="6"
                required
              />
              <span class="scale-number">6</span>
            </label>
            <label class="scale-option" for="rating7">
              <input
                type="radio"
                id="rating7"
                name="control"
                value="7"
                required
              />
              <span class="scale-number">7</span>
            </label>
            <label class="scale-option" for="rating8">
              <input
                type="radio"
                id="rating8"
                name="control"
                value="8"
                required
              />
              <span class="scale-number">8</span>
            </label>
            <label class="scale-option" for="rating9">
              <input
                type="radio"
                id="rating9"
                name="control"
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
              src="${pageContext.request.contextPath}/public/SAM 3/carbon-next-outline.svg"
              alt="Next icon"
            />
          </button>
        </form>
      </main>
    </div>

    <script>
      const form = document.getElementById("controlForm");

      form.addEventListener("submit", (e) => {
        e.preventDefault();

        // Validate rating is selected
        const rating = document.querySelector('input[name="control"]:checked');

        if (!rating) {
          alert("Por favor, selecciona una valoración");
          return;
        }

        // Store control data and proceed to next page
        const surveyData = {
          control: rating.value,
        };

        console.log("Control survey submitted:", surveyData);
        // Here you would typically navigate to the next page
        // window.location.href = 'resultado.html';
      });

      // Optional: Add visual feedback for selected rating
      const radioButtons = document.querySelectorAll('input[name="control"]');
      radioButtons.forEach((radio) => {
        radio.addEventListener("change", (e) => {
          // Remove highlight from all options
          document.querySelectorAll(".scale-option").forEach((opt) => {
            opt.classList.remove("selected");
          });
          // Add highlight to selected option
          e.target.closest(".scale-option").classList.add("selected");
        });
      });
    </script>
  </body>
</html>

