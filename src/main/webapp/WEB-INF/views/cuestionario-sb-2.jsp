<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Cuestionario de Bienestar - SB 2 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cuestionario-sb.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="questionnaire-container">
      <main class="questionnaire-frame">
        <section class="form-header">
          <h1 class="form-title">Cuestionario Sociodemográfico y de Bienestar</h1>
        </section>

        <form class="questionnaire-form" id="questionnaireForm"
          action="${pageContext.request.contextPath}/cuestionario-sb-2" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
          <section class="question-section">
            <h2 class="question-title">En una semana típica ¿con qué frecuencia se siente estresado/a?</h2>
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

          <section class="question-section">
            <h2 class="question-title">En una semana típica ¿con qué frecuencia se siente relajado/a?</h2>
            <div class="radio-group vertical">
              <label class="radio-option">
                <input type="radio" name="relaxation" value="never" required />
                <span class="radio-label">Nunca</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="relaxation" value="sometimes" required />
                <span class="radio-label">De vez en cuando</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="relaxation" value="half-time" required />
                <span class="radio-label">Cerca de la mitad del tiempo</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="relaxation" value="most-time" required />
                <span class="radio-label">La mayor parte del tiempo</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="relaxation" value="always" required />
                <span class="radio-label">Siempre</span>
              </label>
            </div>
          </section>

          <div class="survey-navigation">
            <a class="btn-back" id="sbBackButton" href="${pageContext.request.contextPath}/cuestionario-sb-1"
              aria-label="Regresar">
              <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />
              <span>Regresar</span>
            </a>
            <button type="submit" id="sbNextButton" class="btn-next" aria-label="Siguiente">
              <span>Siguiente</span>
              <img src="${pageContext.request.contextPath}/public/Cuestionario SB 2/carbon-next-outline.svg"
                alt="Next icon" />
            </button>
          </div>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/cuestionario-sb-2.js"></script>
    <script src="${pageContext.request.contextPath}/JavaScript/survey-autoclose.js"></script>
  </body>

  </html>