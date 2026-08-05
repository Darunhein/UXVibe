<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>¡Gracias por participar!</title>
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Actor:wght@400&display=swap"
      rel="stylesheet"
    />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/cheers-bye.css" />
  </head>
  <body>
    <div class="cheers-container">
      <main class="cheers-frame">
        <!-- Header Section -->
        <section class="cheers-header">
          <h1 class="cheers-title">¡Gracias por participar!</h1>
        </section>

        <!-- Success Message Section -->
        <section class="success-section">
          <div class="success-box">
            <img
              src="${pageContext.request.contextPath}/public/Cheers N Bye/lets-icons-check-fill@2x.png"
              alt="Éxito"
              class="success-icon"
              aria-label="Marca de verificación - Éxito"
            />
            <p class="success-message">
              Tus respuestas han sido guardadas correctamente, agradecemos tu
              participación
            </p>
          </div>
        </section>
        <!-- Exit Button -->
        <form class="cheers-exit-form" action="${pageContext.request.contextPath}/complete-test" method="get">
          <button class="btn-exit" type="submit" aria-label="Salir de la aplicación">
            <img
              src="${pageContext.request.contextPath}/public/Cheers N Bye/lets-icons-back-light.svg"
              alt=""
              aria-hidden="true"
            />
            <span>Salir</span>
          </button>
        </form>
      </main>
    </div>
  </body>
</html>
