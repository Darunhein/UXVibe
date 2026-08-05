<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <title>Seleccionar Prueba</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/test-section.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
    />
  </head>
  <body>
    <div class="test-section">
      <main class="test-section__card">
        <div class="test-section__table">
          <div class="test-section__head">
            <div class="test-section__head-cell">Prueba</div>
            <div class="test-section__head-cell">Fecha</div>
            <div class="test-section__head-cell">Ejecutar</div>
            <a class="test-section__add" href="${pageContext.request.contextPath}/html/02%20Selection%20Line/crear_prueba.jsp" aria-label="Crear nueva prueba">
              <img src="${pageContext.request.contextPath}/public/test-section/Group-1.svg" alt="" />
            </a>
          </div>
          <!--
            Populated state reference (from previous test-select view):
            each test row uses four columns in this order:
            1) test name (.test-section__name)
            2) execution date text (.test-section__date)
            3) action button "Ir a la prueba" (.test-section__action)
            4) options/menu button (.test-section__menu)
          -->
            <article class="test-section__row test-section__row--pink">
              <h2 class="test-section__name">Prueba 1</h2>
              <p class="test-section__date">
                La prueba se realizo el 6/06/2026
              </p>
              <button class="test-section__run-button" type="button">
                <img
                  src="../../public/test-section/fluent-cursor-click-20-regular.svg"
                  alt=""
                />
                <span>Ir a la prueba</span>
              </button>
              <button
                class="test-section__menu-button"
                type="button"
                aria-label="Ver opciones de Prueba 1"
              >
                <img src="../../public/test-section/Group-7.svg" alt="" />
              </button>
            </article>
          <section class="test-section__empty" aria-live="polite">
            <img
              class="test-section__arrow"
              src="${pageContext.request.contextPath}/public/test-section/Arrow-2@2x.png"
              alt=""
            />
            <div class="test-section__empty-content">
              <img
                class="test-section__empty-icon"
                src="${pageContext.request.contextPath}/public/test-section/merged-asset-1@2x.png"
                alt="No hay pruebas"
              />
              <h2 class="test-section__empty-title">
                No hay pruebas disponibles por el momento
              </h2>
              <p class="test-section__empty-text">
                Aún no se han registrado pruebas en el sistema.<br />
                Puedes crear una nueva prueba para comenzar
              </p>
            </div>
          </section>
        </div>
        <button class="test-section__logout" aria-label="Cerrar sesión">
          <img
            class="test-section__logout-icon"
            alt=""
            src="${pageContext.request.contextPath}/public/test-section/qlementine-icons-log-out-16.svg"
          />
          <span>Cerrar sesión</span>
        </button>
      </main>
    </div>
  </body>
</html>
