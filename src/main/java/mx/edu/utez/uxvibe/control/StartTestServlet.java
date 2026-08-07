package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@WebServlet(value = "/start-test")
public class StartTestServlet extends HttpServlet {

  private static final String CURRENT_USER_ATTR = "currentUser";
  private static final String CURRENT_TEST_NAME_ATTR = "currentTestName";
  private static final String CURRENT_TEST_STARTED_AT_ATTR =
    "currentTestStartedAt";
  private static final String CURRENT_TEST_COMPLETION_RECORDED_ATTR =
    "currentTestCompletionRecorded";
  private static final String CURRENT_PARTICIPANT_NAME_ATTR =
    "currentParticipantName";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      redirectTo(req, resp, "/login");
      return;
    }

    String testName = req.getParameter("testName");
    if (testName == null || testName.trim().isEmpty()) {
      redirectTo(req, resp, "/tests");
      return;
    }

    session.setAttribute(CURRENT_TEST_NAME_ATTR, testName.trim());
    session.setAttribute(
      CURRENT_TEST_STARTED_AT_ATTR,
      LocalDateTime.now(ZoneId.of("America/Mexico_City"))
    );
    session.setAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR, Boolean.FALSE);
    session.setAttribute(
      CURRENT_PARTICIPANT_NAME_ATTR,
      "Participante " + (System.currentTimeMillis() % 1000)
    );
    redirectTo(
      req,
      resp,
      "/html/03%20Execution%20Line/terminos-y-condiciones.jsp"
    );
  }

  private void redirectTo(
    HttpServletRequest req,
    HttpServletResponse resp,
    String path
  ) {
    try {
      resp.sendRedirect(req.getContextPath() + path);
    } catch (IOException e) {
      try {
        resp.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "No se pudo redirigir a la ruta solicitada."
        );
      } catch (IOException ignored) {
        // Fall through since the response is already in a failed state.
      }
    }
  }
}
