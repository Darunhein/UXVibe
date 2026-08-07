package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import mx.edu.utez.uxvibe.model.ParticipantItem;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/participants")
public class ParticipantsServlet extends HttpServlet {

  private static final String PARTICIPANTS_VIEW =
    "/WEB-INF/views/participants.jsp";
  private static final String CURRENT_USER_ATTR = "currentUser";
  private static final String CURRENT_TEST_NAME_ATTR = "currentTestName";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      try {
        resp.sendRedirect(req.getContextPath() + "/login");
      } catch (IOException e) {
        try {
          resp.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "No se pudo redirigir al inicio de sesión."
          );
        } catch (IOException ignored) {
          // Fall through since the response is already in a failed state.
        }
      }
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    String selectedTestName = resolveTestName(
      req.getParameter("testName"),
      (String) session.getAttribute(CURRENT_TEST_NAME_ATTR)
    );
    if (!selectedTestName.isEmpty()) {
      session.setAttribute(CURRENT_TEST_NAME_ATTR, selectedTestName);
    }

    List<ParticipantItem> allParticipants =
      ParticipantStore.getInstance().listByUserAndTest(
        account.getEmail(),
        selectedTestName
      );
    int totalParticipants = allParticipants.size();
    int totalPages = PaginationSupport.countPages(
      totalParticipants,
      PaginationSupport.PAGE_SIZE
    );
    int currentPage = PaginationSupport.resolvePage(
      req.getParameter("page"),
      totalPages
    );

    req.setAttribute(
      "participants",
      PaginationSupport.paginate(
        allParticipants,
        currentPage,
        PaginationSupport.PAGE_SIZE
      )
    );
    req.setAttribute("selectedTestName", selectedTestName);
    req.setAttribute("currentPage", currentPage);
    req.setAttribute("totalPages", totalPages);
    req.setAttribute(
      "pageHrefPrefix",
      buildPageHrefPrefix(req, selectedTestName)
    );
    try {
      req.getRequestDispatcher(PARTICIPANTS_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      try {
        resp.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "No se pudo mostrar la página de participantes."
        );
      } catch (IOException ignored) {
        // Fall through since the response is already in a failed state.
      }
    }
  }

  private String resolveTestName(String parameterValue, String sessionValue) {
    String resolved = normalize(parameterValue);
    if (!resolved.isEmpty()) {
      return resolved;
    }
    return normalize(sessionValue);
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim();
  }

  private String encodeQueryValue(String value) {
    if (value == null) {
      return "";
    }
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      return value.replace(" ", "+");
    }
  }

  private String buildPageHrefPrefix(
    HttpServletRequest req,
    String selectedTestName
  ) throws IOException {
    StringBuilder builder = new StringBuilder();
    builder.append(req.getContextPath()).append("/participants?");
    if (!selectedTestName.isEmpty()) {
      builder
        .append("testName=")
        .append(encodeQueryValue(selectedTestName))
        .append("&");
    }
    builder.append("page=");
    return builder.toString();
  }
}
