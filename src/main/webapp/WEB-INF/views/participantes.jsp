<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="mx.edu.utez.uxvibe.model.ParticipantItem" %>
<%!
  private String escapeHtml(String value) {
    if (value == null) { return ""; }
    return value
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }
%>
<%
  List<ParticipantItem> participants = new ArrayList<ParticipantItem>();
  Object participantsAttribute = request.getAttribute("participants");
  if (participantsAttribute instanceof List<?>) {
    for (Object item : (List<?>) participantsAttribute) {
      if (item instanceof ParticipantItem) {
        participants.add((ParticipantItem) item);
      }
    }
  }
  boolean hasParticipants = !participants.isEmpty();
  Integer currentPage = (Integer) request.getAttribute("currentPage");
  Integer totalPages  = (Integer) request.getAttribute("totalPages");
  String selectedTestName = (String) request.getAttribute("selectedTestName");
  String pageHrefPrefix   = (String) request.getAttribute("pageHrefPrefix");
%>
<!doctype html>
<html lang="es">

<head>
  <title>Participantes - UX Vibe</title>
  <meta charset="utf-8" />
  <meta name="viewport" content="initial-scale=1, width=device-width" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/participantes.css" />
  <link rel="stylesheet"
    href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" />
</head>

<body>
  <div class="participantes-screen">
    <main class="participantes-card">
      <section class="participantes-table" aria-label="Tabla de participantes">
        <header class="participantes-head">
          <div class="participantes-head-cell">Nombre</div>
          <div class="participantes-head-cell">Descripción</div>
          <div class="participantes-head-cell">Duración</div>
          <div class="participantes-head-cell">Estadísticas</div>
        </header>

        <% if (hasParticipants) { %>
          <section class="participantes-body" aria-live="polite">
            <% String[] rowClasses = {"participantes-row--pink", "participantes-row--blue", "participantes-row--sand"};
               for (int i = 0; i < participants.size(); i++) {
                 ParticipantItem participant = participants.get(i); %>


            <article class="participantes-row <%= rowClasses[i % rowClasses.length] %>">
                <h2 class="participante-nombre">
                  <%= escapeHtml(participant.getName()) %>
                </h2>
                <p class="participante-descripcion">
                  <%= escapeHtml(participant.getDescription()) %>
                </p>
                <div class="participante-duracion">
                  <img
                    src="${pageContext.request.contextPath}/public/participantes/weui-time-outlined.svg"
                    alt="" />
                  <span>
                    <%= participant.getDurationLabel() %>
                  </span>
                </div>

              <div class="participante-actions">
                <a
                        class="participante-detalles"
                        href="${pageContext.request.contextPath}/participant-report?testName=<%= java.net.URLEncoder.encode(selectedTestName == null ? "" : selectedTestName, "UTF-8") %>&participantName=<%= java.net.URLEncoder.encode(participant.getName(), "UTF-8") %>"
                        aria-label="Ver estadísticas de <%= escapeHtml(participant.getName()) %>">
                  <span>Detalles</span>
                  <img
                          src="${pageContext.request.contextPath}/public/participantes/ant-design-ellipsis-outlined.svg"
                          alt="" />
                </a>
                <form class="participante-delete"
                      action="${pageContext.request.contextPath}/delete-participant"
                      method="post">
                  <input type="hidden"
                         name="testName"
                         value="<%= selectedTestName == null ? "" : selectedTestName %>" />
                  <input type="hidden"
                         name="participantName"
                         value="<%= participant.getName() %>" />
                  <button class="participante-delete-button"
                          type="submit">
                    Borrar
                  </button>
                </form>
              </div>

              </article>


            <% } %>
          </section>
          <% if (totalPages != null && totalPages > 1) { %>
            <nav class="participantes-pagination" aria-label="Paginación de participantes">
              <% if (currentPage > 1) { %>
                <a class="participantes-pagination-link participantes-pagination-link--control"
                  href="<%= pageHrefPrefix %><%= currentPage - 1 %>">Anterior</a>
              <% } else { %>
                <span
                  class="participantes-pagination-link participantes-pagination-link--control participantes-pagination-link--disabled"
                  aria-disabled="true">Anterior</span>
              <% } %>

              <% for (int pageNumber = 1; pageNumber <= totalPages; pageNumber++) { %>
                <a class="participantes-pagination-link <%= pageNumber == currentPage ? "participantes-pagination-link--active" : "" %>"
                  href="<%= pageHrefPrefix %><%= pageNumber %>"
                  <%= pageNumber == currentPage ? "aria-current=\"page\"" : "" %>>
                  <%= pageNumber %>
                </a>
              <% } %>

              <% if (currentPage < totalPages) { %>
                <a class="participantes-pagination-link participantes-pagination-link--control"
                  href="<%= pageHrefPrefix %><%= currentPage + 1 %>">Siguiente</a>
              <% } else { %>
                <span
                  class="participantes-pagination-link participantes-pagination-link--control participantes-pagination-link--disabled"
                  aria-disabled="true">Siguiente</span>
              <% } %>
            </nav>
          <% } %>
        <% } else { %>
          <section class="participantes-empty" aria-live="polite">
            <div class="participantes-empty-content">
              <img class="participantes-empty-icon"
                src="${pageContext.request.contextPath}/public/test-section/merged-asset-1@2x.png"
                alt="No hay participantes" />
              <h2 class="participantes-empty-title">No hay participantes disponibles por el momento</h2>
              <p class="participantes-empty-text">
                Aún no hay pruebas completadas<%= selectedTestName != null && !selectedTestName.trim().isEmpty() ? " para " + escapeHtml(selectedTestName) : "" %>.<br />
                Cuando finalicen una prueba, aquí aparecerán sus participantes
              </p>
            </div>
          </section>
        <% } %>
      </section>

      <a class="participantes-back" href="${pageContext.request.contextPath}/tests">
        <img src="${pageContext.request.contextPath}/public/participantes/lets-icons-back-light.svg"
          alt="" />
        <span>Regresar</span>
      </a>
    </main>
  </div>
  <script src="${pageContext.request.contextPath}/JavaScript/participantes.js"></script>
</body>

</html>