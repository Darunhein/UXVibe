<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Encuesta de Satisfacción</title>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Afacad:wght@400&display=swap"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/encuesta-survey.css"
    />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <div class="survey-container">
      <main class="survey-frame">
        <!-- Survey Header Section -->
        <section class="survey-header">
          <div class="header-left">
            <h1 class="survey-title">Encuesta de Satisfacción</h1>
            <p class="survey-instructions">
              Evalúa tu experiencia con nuestra plataforma web seleccionando la
              opción que mejor represente tu opinión.
            </p>
          </div>
          <div class="scale-legend">
            <p>1 = Totalmente en desacuerdo.</p>
            <p>2 = En desacuerdo.</p>
            <p>3 = Neutral.</p>
            <p>4 = De acuerdo.</p>
            <p>5 = Totalmente de acuerdo.</p>
          </div>
        </section>

        <!-- Survey Questions Section -->
        <form
          class="survey-form"
          id="surveyForm"
          name="satisfaction"
          action="${pageContext.request.contextPath}/survey-submit"
          method="post"
        >
          <div class="questions-container">
            <!-- Question 1 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">1.-</span>
                <span
                  >Me resultó fácil aprender a navegar por esta página/sistema
                  web.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q1"
                    value="1"
                    required
                    aria-label="Totalmente en desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">1</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q1"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q1"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q1"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q1"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 2 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">2.-</span>
                <span
                  >La estructura del menú y los enlaces es intuitiva y sé dónde
                  encontrar la información.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q2"
                    value="1"
                    required
                    aria-label="Totalmente en desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">1</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q2"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q2"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q2"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q2"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 3 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">3.-</span>
                <span
                  >Las funciones y herramientas de la página cubren
                  completamente mis necesidades.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q3"
                    value="1"
                    required
                    aria-label="Totalmente en desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">1</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q3"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q3"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q3"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q3"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 4 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">4.-</span>
                <span
                  >Este sistema web me permite realizar mis tareas de forma más
                  rápida y eficiente.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q4"
                    value="1"
                    required
                    aria-label="Totalmente en desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">1</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q4"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q4"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q4"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q4"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 5 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">5.-</span>
                <span
                  >Creo que el sistema web es innecesariamente complejo o
                  difícil de entender.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q5"
                    value="1"
                    required
                    aria-label="Totalmente en desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">1</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q5"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q5"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q5"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q5"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Next Button -->
          <button
            type="button"
            id="surveyNextButton"
            class="btn-next"
            aria-label="Siguiente"
          >
            <span>Siguiente</span>
            <img
              src="${pageContext.request.contextPath}/public/encuesta 1/carbon-next-outline.svg"
              alt=""
              aria-hidden="true"
            />
          </button>
        </form>
        <a
          class="btn-back"
          href="javascript:history.back()"
          id="surveyBackButton"
          aria-label="Regresar a la página anterior"
        >
          <img
            src="${pageContext.request.contextPath}/public/encuesta 1/lets-icons-back-light.svg"
            alt=""
            aria-hidden="true"
          />
          <span>Regresar</span>
        </a>
      </main>
    </div>

    <script src="${pageContext.request.contextPath}/JavaScript/encuesta-1.js"></script>
  </body>
</html>
