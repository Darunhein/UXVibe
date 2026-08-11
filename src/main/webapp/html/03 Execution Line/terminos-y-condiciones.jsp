<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="initial-scale=1, width=device-width"/>
    <title>Términos y Condiciones</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/terminos-condiciones.css"/>
    <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/css2?family=ADLaM+Display:wght@400&display=swap"
    />
    <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400&display=swap"
    />
    <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/css2?family=Actor:wght@400&display=swap"
    />
</head>
<body>
<div class="terminos-container">
    <main class="terminos-frame">
        <!-- Header Section -->
        <section class="terminos-header">
            <h1 class="terminos-title">Términos y condiciones</h1>
        </section>

        <!-- Terms Content Section -->
        <div class="terms-content">
            <div class="terms-box">
                <img
                        class="terms-icon"
                        src="${pageContext.request.contextPath}/public/terminos y condiciones/Star-1.svg"
                        alt="Star icon"
                />
                <div class="terms-text">
                    Al utilizar este sistema, el usuario acepta el tratamiento de sus
                    datos personales de conformidad con la Ley General de Protección
                    de Datos Personales y el Acuerdo de Uso de Datos Personales. La
                    información proporcionada será utilizada únicamente para los fines
                    relacionados con el funcionamiento del sistema y será resguardada
                    de manera confidencial y segura. El uso continuo de la plataforma
                    implica la aceptación de estos términos y condiciones.
                </div>
            </div>
        </div>

        <!-- Acceptance Section -->
        <div class="acceptance-section">
            <label class="acceptance-label">
                <input
                        type="checkbox"
                        id="acceptanceCheckbox"
                        name="acceptance"
                        class="acceptance-checkbox"
                        required
                />
                <span class="acceptance-text">Acepto</span>
            </label>
        </div>

        <!-- Action Buttons Section -->
        <div class="buttons-section">
            <a id="btnMicrofono" class="btn-secondary"
               href="${pageContext.request.contextPath}/html/03%20Execution%20Line/prueba-microfono.jsp">Iniciar prueba
                de micrófono</a>
            <a id="btnComenzar" class="btn-primary disabled"
               href="${pageContext.request.contextPath}/html/03%20Execution%20Line/grabacion-de-prueba.jsp">Comenzar
                prueba</a>
        </div>

        <!-- Back Button -->
        <a class="btn-back" href="${pageContext.request.contextPath}/tests">
            <img
                    src="${pageContext.request.contextPath}/public/terminos y condiciones/lets-icons-back-light.svg"
                    alt="Back"
            />
            <span>Regresar</span>
        </a>
    </main>
</div>
<script src="${pageContext.request.contextPath}/JavaScript/TermsNConditions.js"></script>
</body>
</html>
