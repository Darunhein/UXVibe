<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <title>Cuestionario Sociodemográfico y de Bienestar - SB 1</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/global.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/CSS/cuestionario-sb.css"
    />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap"
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
        <form
          class="questionnaire-form"
          id="questionnaireForm"
          action="${pageContext.request.contextPath}/html/04%20SAM%20Line/cuestionario-sb-2.jsp"
          method="get"
        >
          <!-- Personal Information Section -->
          <section class="form-section">
            <div class="form-row">
              <!-- Full Name Input -->
              <div class="form-group">
                <div class="input-label-group">
                  <img
                    src="${pageContext.request.contextPath}/public/Cuestionario SB 1/merged-asset-1@2x.png"
                    alt="Name icon"
                    class="input-icon"
                  />
                  <label for="fullName" class="input-label"
                    >Nombre completo</label
                  >
                </div>
                <input
                  type="text"
                  id="fullName"
                  name="fullName"
                  class="form-input-name"
                  placeholder="Escribe tu nombre completo"
                  required
                />
              </div>
              <!-- Age Input -->
              <div class="form-group">
                <div class="input-label-group">
                  <label for="age" class="input-label">Edad</label>
                </div>
                <input
                  type="number"
                  id="age"
                  name="age"
                  class="form-input-age"
                  placeholder="Ingresa tu edad"
                  min="0"
                  required
                />
              </div>
            </div>
          </section>

          <!-- Gender Section -->
          <section class="form-section gender-section">
            <div class="section-header">
              <img
                src="${pageContext.request.contextPath}/public/Cuestionario SB 1/Group-49@2x.png"
                alt="Gender icon"
                class="section-icon"
              />
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

          <!-- Education Level Section -->
          <section class="form-section education-section">
            <div class="section-header">
              <img
                src="${pageContext.request.contextPath}/public/Cuestionario SB 1/Group-51@2x.png"
                alt="Education icon"
                class="section-icon"
              />
              <h2 class="section-title">Nivel máximo de Educación</h2>
            </div>
            <div class="education-grid">
              <label class="radio-option">
                <input type="radio" name="education" value="basic" required />
                <span class="radio-label">Básico (Primaria)</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="education"
                  value="secondary"
                  required
                />
                <span class="radio-label">Medio (Secundaria)</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="education"
                  value="preparatory"
                  required
                />
                <span class="radio-label">Medio Superior (Preparatoria)</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="education"
                  value="university"
                  required
                />
                <span class="radio-label">Superior (Universidad)</span>
              </label>
              <label class="radio-option">
                <input type="radio" name="education" value="masters" required />
                <span class="radio-label">Superior (Maestría)</span>
              </label>
              <label class="radio-option">
                <input
                  type="radio"
                  name="education"
                  value="doctorate"
                  required
                />
                <span class="radio-label">Superior (Doctorado)</span>
              </label>
            </div>
          </section>

          <!-- Next Button -->
          <button
            type="button"
            id="sbNextButton"
            class="btn-next"
            aria-label="Siguiente"
            onclick="
              var fullName = document.getElementById('fullName').value.trim();
              var age = document.getElementById('age').value;
              var gender = document.querySelector(
                'input[name=&quot;gender&quot;]:checked',
              );
              var education = document.querySelector(
                'input[name=&quot;education&quot;]:checked',
              );
              if (!fullName || !age || !gender || !education) {
                alert('Por favor, completa todos los campos');
                return;
              }
              window.location.href =
                '${pageContext.request.contextPath}/html/04%20SAM%20Line/cuestionario-sb-2.jsp';
            "
          >
            <span>Siguiente</span>
            <img
              src="${pageContext.request.contextPath}/public/Cuestionario SB 1/carbon-next-outline.svg"
              alt="Next icon"
            />
          </button>
          <a
            class="btn-back"
            href="#"
            id="sbBackButton"
            aria-label="Regresar a la página anterior"
          >
            <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg" alt="" />              alt=""
              aria-hidden="true"
            <span>Regresar</span>
          </a>
        </form>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/JavaScript/cuestionario-sb-1.js"></script>
    <script src="${pageContext.request.contextPath}/JavaScript/text-only-validation.js"></script>
  </body>
</html>
