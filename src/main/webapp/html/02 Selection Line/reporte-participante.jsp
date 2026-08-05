<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <title>reporte de participante</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />

    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/reporte-participante.css" />
    <link
      rel="stylesheet"
      href="https://fonts.googleapis.com/css2?family=Inter:wght@300;600;700;800&display=swap"
    />
  </head>
  <body>
    <div class="reporte-page">
      <section class="reporte-header">
        <img
          class="reporte-user-icon"
          src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-user-light@2x.png"
          alt="Participante"
        />
        <div class="reporte-user-info">
          <div class="reporte-user-name">Jessica</div>
          <div class="reporte-meta-row">
            <img
              class="reporte-time-icon"
              src="${pageContext.request.contextPath}/public/reporte-participante/weui-time-outlined.svg"
              alt=""
            />
            <span>5 min</span>
            <span class="reporte-meta-divider" aria-hidden="true"></span>
            <span>Completada el 6/06/2026, 10:30 a.m.</span>
          </div>
          <div class="reporte-title-inline">Reporte de resultados</div>
        </div>
      </section>

      <main class="reporte-body">
        <section class="reporte-charts-row">
          <article class="reporte-chart-card">
            <h2>Resultado general</h2>
            <img
              src="${pageContext.request.contextPath}/public/reporte-participante/streamline-block-other-ui-graph@2x.png"
              alt="Gráfico de resultado general"
            />
          </article>

          <article class="reporte-chart-card reporte-chart-card--bars">
            <h2>Desempeño por sección</h2>
            <div class="reporte-bars">
              <div class="reporte-bar reporte-bar--light"></div>
              <div class="reporte-bar reporte-bar--pink"></div>
              <div class="reporte-bar reporte-bar--blue"></div>
            </div>
          </article>
        </section>

        <section class="reporte-block-1">
          <h2>Detalles de las preguntas</h2>
          <div class="reporte-box reporte-box--summary">
            Los resultados indican una percepción positiva de la encuesta. El
            usuario calificó satisfactoriamente la mayoría de los aspectos
            evaluados, aunque existen oportunidades de mejora para optimizar la
            experiencia.
          </div>
        </section>

        <section class="reporte-block-2">
          <h2>Información de la prueba</h2>
          <table
            class="reporte-box reporte-box--info"
            aria-label="Resumen de prueba"
          >
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Fecha de finalización</th>
                <th>Duración de la prueba</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Jessica</td>
                <td>6/06/2026, 10:30 a.m.</td>
                <td>5 minutos</td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>

      <button class="reporte-back" type="button">
        <img
          src="${pageContext.request.contextPath}/public/reporte-participante/lets-icons-back-light.svg"
          alt=""
        />
        <span>Regresar</span>
      </button>
    </div>
  </body>
</html>

