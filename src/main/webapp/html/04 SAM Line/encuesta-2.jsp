<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Encuesta de Satisfacción - Parte 2</title>
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
              Por favor, evalúa las siguientes afirmaciones respecto a tu
              experiencia con nuestra plataforma web, seleccionando la opción
              que mejor se adapte a tu opinión.
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
          name="satisfaction2"
          action="${pageContext.request.contextPath}/survey-submit"
          method="post"
        >
          <div class="questions-container">
            <!-- Question 6 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">6.-</span>
                <span
                  >La información, textos y contenidos que ofrece la plataforma
                  son claros y valiosos.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q6"
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
                    name="q6"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q6"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q6"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q6"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 7 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">7.-</span>
                <span
                  >El diseño visual de la página web es atractivo, limpio y
                  moderno.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q7"
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
                    name="q7"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q7"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q7"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q7"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 8 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">8.-</span>
                <span
                  >El tamaño de la letra, los contrastes y los colores facilitan
                  una lectura cómoda.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q8"
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
                    name="q8"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q8"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q8"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q8"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 9 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">9.-</span>
                <span
                  >La interfaz se siente saturada, desordenada o visualmente
                  confusa.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q9"
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
                    name="q9"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q9"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q9"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q9"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>

            <!-- Question 10 -->
            <div class="question-row">
              <div class="question-text">
                <span class="question-number">10.-</span>
                <span
                  >La página web carga rápidamente y las secciones responden sin
                  retrasos.</span
                >
              </div>
              <div class="scale-options">
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q10"
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
                    name="q10"
                    value="2"
                    aria-label="En desacuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">2</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q10"
                    value="3"
                    aria-label="Neutral"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">3</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q10"
                    value="4"
                    aria-label="De acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">4</span>
                </label>
                <label class="scale-option">
                  <input
                    type="radio"
                    name="q10"
                    value="5"
                    aria-label="Totalmente de acuerdo"
                  />
                  <span class="scale-dot"></span>
                  <span class="scale-number">5</span>
                </label>
              </div>
            </div>
          </div>

          <div class="survey-navigation">

            <a
                    class="btn-back"
                    href="#"
                    id="surveyBackButton"
                    aria-label="Regresar a la página anterior"
            >
              <img
                      src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt=""

              />
              <span>Regresar</span>
            </a>

            <button
                    type="button"
                    id="surveyNextButton"
                    class="btn-next"
                    aria-label="Siguiente"
                    onclick="
                            (function () {
                            var form = document.getElementById('surveyForm');
                            var questions = ['q6', 'q7', 'q8', 'q9', 'q10'];

                            for (var i = 0; i < questions.length; i++) {
                            var selected = form.querySelector(
                            'input[name=&quot;' + questions[i] + '&quot;]:checked'
                            );

                            if (!selected) {
                            alert('Por favor, responde todas las preguntas antes de seguir.');
                            return;
                            }
                            }

                            fetch('${pageContext.request.contextPath}/survey-submit', {
                            method: 'POST',
                            body: new FormData(form),
                            }).then(function () {
                            window.location.href =
                            '${pageContext.request.contextPath}/html/04%20SAM%20Line/encuesta-3.jsp';
                            });
                            })()
                            "
            >
              <span>Siguiente</span>

              <img
                      src="${pageContext.request.contextPath}/public/encuesta 2/carbon-next-outline.svg"
                      alt=""
                      aria-hidden="true"
              />
            </button>

          </div>

        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/encuesta-2.js"></script>
  </body>
</html>


