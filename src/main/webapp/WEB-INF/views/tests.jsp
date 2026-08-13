<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="mx.edu.utez.uxvibe.model.TestItem" %>
<%!
  private String escapeHtml(String value) {
    if (value == null) {
      return "";
    }
    return value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }
%>
<%
  List<TestItem> tests = new ArrayList<>();
  Object testsAttribute = request.getAttribute("tests");
  if (testsAttribute instanceof List<?>) {
    for (Object item : (List<?>) testsAttribute) {
      if (item instanceof TestItem) {
        tests.add((TestItem) item);
      }
    }
  }
  boolean hasTests = !tests.isEmpty();
  Integer currentPage = (Integer) request.getAttribute("currentPage");
  Integer totalPages = (Integer) request.getAttribute("totalPages");
%>
<!doctype html>
<html lang="es">
  <head>
    <title>Seleccionar Prueba UX Vibe</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="initial-scale=1, width=device-width" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/test-section.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
  </head>
  <body>
    <div class="test-section">
      <main class="test-section__card">
        <div class="test-section__table">
          <div class="test-section__head">
            <div class="test-section__head-cell">Prueba</div>
            <div class="test-section__head-cell">Fecha</div>
            <div class="test-section__head-cell">Ejecutar</div>
            <a class="test-section__add" href="${pageContext.request.contextPath}/create-test" aria-label="Crear nueva prueba">
              <img src="${pageContext.request.contextPath}/public/test-section/Group-1.svg" alt="" />
            </a>
          </div>
          <% if (hasTests) { %>
            <section class="test-section__rows" aria-live="polite">
              <%
                  String[] rowClasses = {"test-section__row--pink", "test-section__row--blue", "test-section__row--sand"};
                  for (int i = 0; i < tests.size(); i++) {
                    TestItem test = tests.get(i);
                    String escapedTestName = escapeHtml(test.getName());
              %>
                <article class="test-section__row <%= rowClasses[i % rowClasses.length] %>">
                  <h2 class="test-section__name"><%= escapedTestName %></h2>
                  <p class="test-section__date">
                    La prueba se realizo el <%= test.getCreatedOnFormatted() %>
                  </p>
                  <form class="test-section__action-form test-section__action-form--run" action="${pageContext.request.contextPath}/start-test" method="get">
                    <input type="hidden" name="testName" value="<%= escapedTestName %>" />
                    <button class="test-section__run-button" type="submit" aria-label="Ir a la prueba <%= escapedTestName %>">
                      <img src="${pageContext.request.contextPath}/public/test-section/fluent-cursor-click-20-regular.svg" alt="" />
                      <span>Ir a la prueba</span>
                    </button>
                  </form>
                  <form class="test-section__action-form test-section__action-form--menu" action="${pageContext.request.contextPath}/participants" method="get">
                    <input type="hidden" name="testName" value="<%= escapedTestName %>" />
                    <button class="test-section__menu-button" type="submit" aria-label="Ver participantes de <%= escapedTestName %>">
                      <img src="${pageContext.request.contextPath}/public/test-section/Group-7.svg" alt="" />
                    </button>
                  </form>
                  <form class="test-section__action-form test-section__action-form--delete" action="${pageContext.request.contextPath}/delete-test" method="post" onsubmit="return confirm('¿Borrar la prueba &quot;'+ document.getElementsByName('testName' )[0].value + '&quot;? Esta acción eliminará participantes y respuestas.')">
                    <input type="hidden" name="testName" value="<%= escapedTestName %>" />
                    <button class="test-section__delete-button" type="submit" aria-label="Borrar prueba <%= escapedTestName %>">Borrar</button>
                  </form>
                </article>
              <% } %>
            </section>
            <% if (totalPages != null && totalPages > 1) { %>
              <nav class="test-section__pagination" aria-label="Paginación de pruebas">
                <% if (currentPage > 1) { %>
                  <a class="test-section__pagination-link test-section__pagination-link--control" href="${pageContext.request.contextPath}/tests?page=<%= currentPage - 1 %>">Anterior</a>
                <% } else { %>
                  <span class="test-section__pagination-link test-section__pagination-link--control test-section__pagination-link--disabled" aria-disabled="true">Anterior</span>
                <% } %>

                <%
                    for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) { %>
                  <a
                    class="test-section__pagination-link <%= pageNumber == currentPage ? "test-section__pagination-link--active" : "" %>"
                    href="${pageContext.request.contextPath}/tests?page=<%= pageNumber %>"
                    <%= pageNumber == currentPage ? "aria-current=\"page\"" : "" %>>
                    <%= pageNumber %>
                  </a>
                <% } %>

                <% if (currentPage < totalPages) { %>
                  <a class="test-section__pagination-link test-section__pagination-link--control" href="${pageContext.request.contextPath}/tests?page=<%= currentPage + 1 %>">Siguiente</a>
                <% } else { %>
                  <span class="test-section__pagination-link test-section__pagination-link--control test-section__pagination-link--disabled" aria-disabled="true">Siguiente</span>
                <% } %>
              </nav>
            <% } %>
          <% } else { %>
            <section class="test-section__empty" aria-live="polite">
              <img class="test-section__arrow" src="${pageContext.request.contextPath}/public/test-section/Arrow-2@2x.png" alt="" />
              <div class="test-section__empty-content">
                <img class="test-section__empty-icon" src="${pageContext.request.contextPath}/public/test-section/merged-asset-1@2x.png" alt="No hay pruebas" />
                <h2 class="test-section__empty-title">No hay pruebas disponibles por el momento</h2>
                <p class="test-section__empty-text">Aún no se han registrado pruebas en el sistema.<br />Puedes crear una nueva prueba para comenzar</p>
              </div>
            </section>
          <% } %>
        </div>
        <a class="test-section__logout" href="${pageContext.request.contextPath}/logout">
          <img class="test-section__logout-icon" alt="" src="${pageContext.request.contextPath}/public/test-section/qlementine-icons-log-out-16.svg" />
          <span>Cerrar sesión</span>
        </a>
      </main>
    </div>
  </body>
</html>
