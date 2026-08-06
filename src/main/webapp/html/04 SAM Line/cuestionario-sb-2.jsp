<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Cuestionario Sociodemográfico y de Bienestar - SB 2</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cuestionario-sb.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Afacad:wght@400&display=swap"
    />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="questionnaire-container">
      <main class="questionnaire-frame">
        <!-- Title Section -->
        <section class="form-header">
          <h1 class="form-title">
            Cuestionario Sociodemográfico y de Bienestar
          </h1>
        </section>

        <!-- Form Content -->
        <form class="questionnaire-form" id="questionnaireForm" action="${pageContext.request.contextPath}/html/04%20SAM%20Line/sam-1.jsp" method="get">
          <!-- Stress Question Section -->
          <section class="question-section">
            <h2 class="question-title">
              En una semana típica ¿con que frecuencia se siente estresado/a?
            </h2>
            <div class="radio-group vertical">
              <label class="radio-option">
                <input type="radio" name="stress" value="never" required />
                <span class="radio-label">Nunca</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="stress" value="sometimes" required />
                <span class="radio-label">De vez en cuando</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="stress" value="half-time" required />
                <span class="radio-label">Cerca de la mitad del tiempo</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="stress" value="most-time" required />
                <span class="radio-label">La mayor parte del tiempo</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="stress" value="always" required />
                <span class="radio-label">Siempre</span>
              </label>
            </div>
          </section>

          <!-- Relaxation Question Section -->
          <section class="question-section">
            <h2 class="question-title">
              En una semana típica ¿con que frecuencia se siente relajado/a?
            </h2>
            <div class="radio-group vertical">
              <label class="radio-option">
                <input type="radio" name="relaxation" value="never" required />
                <span class="radio-label">Nunca</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="relaxation"
                  value="sometimes"
                  required
                />
                <span class="radio-label">De vez en cuando</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="relaxation"
                  value="half-time"
                  required
                />
                <span class="radio-label">Cerca de la mitad del tiempo</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="relaxation"
                  value="most-time"
                  required
                />
                <span class="radio-label">La mayor parte del tiempo</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="relaxation" value="always" required />
                <span class="radio-label">Siempre</span>
              </label>
            </div>
          </section>

          <!-- Next Button -->
          <button type="submit" class="btn-next" aria-label="Siguiente">
            <span>Siguiente</span>
            <img
              src="${pageContext.request.contextPath}/public/Cuestionario SB 2/carbon-next-outline.svg"
              alt="Next icon"
            />
          </button>
          <a class="btn-back" href="${pageContext.request.contextPath}/html/03%20Execution%20Line/grabacion-de-prueba.jsp" aria-label="Regresar a grabación de prueba">
            <img
              src="${pageContext.request.contextPath}/public/Cuestionario SB 2/lets-icons-back-light.svg"
              alt=""
              aria-hidden="true"
            />
            <span>Regresar</span>
          </a>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/cuestionario-sb-2.js"></script>
  </body>
</html>
