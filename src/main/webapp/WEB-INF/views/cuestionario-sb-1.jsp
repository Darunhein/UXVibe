<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!doctype html>
  <html lang="es">

  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Cuestionario Sociodemográfico - SB 1 - UX Vibe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cuestionario-sb.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" />
  </head>

  <body data-context-path="${pageContext.request.contextPath}">
    <div class="questionnaire-container">
      <main class="questionnaire-frame">
        <section class="form-header">
          <h1 class="form-title">Cuestionario Sociodemográfico y de Bienestar</h1>
        </section>

        <form class="questionnaire-form" id="questionnaireForm"
          action="${pageContext.request.contextPath}/cuestionario-sb-1" method="post">
          <%@ include file="/WEB-INF/views/_csrf.jsp" %>
            <section class="form-section">
              <div class="form-row">
                <div class="form-group">
                  <div class="input-label-group">
                    <img src="${pageContext.request.contextPath}/public/Cuestionario SB 1/merged-asset-1@2x.png"
                      alt="Name icon" class="input-icon" />
                    <label for="fullName" class="input-label">Nombre completo</label>
                  </div>
                  <input type="text" id="fullName" name="fullName" class="form-input-name"
                    placeholder="Escribe tu nombre completo" required />
                </div>
                <div class="form-group">
                  <div class="input-label-group">
                    <label for="age" class="input-label">Edad</label>
                  </div>
                  <input type="number" id="age" name="age" class="form-input-age" placeholder="Ingresa tu edad" min="3"
                    max="120" required />
                </div>
              </div>
            </section>

            <section class="form-section gender-section">
              <div class="section-header">
                <img src="${pageContext.request.contextPath}/public/Cuestionario SB 1/Group-49@2x.png" alt="Gender icon"
                  class="section-icon" />
                <h2 class="section-title">Sexo</h2>
              </div>
              <div class="radio-group">
                <label class="radio-option">
                  <input type="radio" name="gender" value="masculine" required />
                  <span class="radio-label">Masculino</span>
                </label>
                <label class="radio-option">
                  <input type="radio" name="gender" value="feminine" required />
                  <span class="radio-label">Femenino</span>
                </label>
              </div>
            </section>

            <section class="form-section education-section">
              <div class="section-header">
                <img src="${pageContext.request.contextPath}/public/Cuestionario SB 1/Group-51@2x.png"
                  alt="Education icon" class="section-icon" />
                <h2 class="section-title">Nivel máximo de Educación</h2>
              </div>
              <div class="education-grid">
                <label class="radio-option">
                  <input type="radio" name="education" value="basic" required />
                  <span class="radio-label">Básico (Primaria)</span>
                </label>

                <label class="radio-option">
                  <input type="radio" name="education" value="university" required />
                  <span class="radio-label">Superior (Universidad)</span>
                </label>

                <label class="radio-option">
                  <input type="radio" name="education" value="secondary" required />
                  <span class="radio-label">Medio (Secundaria)</span>
                </label>

                <label class="radio-option">
                  <input type="radio" name="education" value="masters" required />
                  <span class="radio-label">Superior (Maestría)</span>
                </label>

                <label class="radio-option">
                  <input type="radio" name="education" value="preparatory" required />
                  <span class="radio-label">Medio Superior (Preparatoria)</span>
                </label>

                <label class="radio-option">
                  <input type="radio" name="education" value="doctorate" required />
                  <span class="radio-label">Superior (Doctorado)</span>
                </label>
              </div>
            </section>

            <div class="survey-navigation">
              <a class="btn-back" id="sbBackButton" href="${pageContext.request.contextPath}/test-recording"
                aria-label="Regresar">
                <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />
                <span>Regresar</span>
              </a>
              <button type="submit" id="sbNextButton" class="btn-next" aria-label="Siguiente">
                <span>Siguiente</span>
                <img src="${pageContext.request.contextPath}/public/Cuestionario SB 1/carbon-next-outline.svg"
                  alt="Next icon" />
              </button>
            </div>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/cuestionario-sb-1.js"></script>
  </body>

  </html>