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
  private static final String CURRENT_TEST_STARTED_AT_ATTR = "currentTestStartedAt";
  private static final String CURRENT_TEST_COMPLETION_RECORDED_ATTR = "currentTestCompletionRecorded";
  private static final String CURRENT_PARTICIPANT_NAME_ATTR = "currentParticipantName";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    String testName = req.getParameter("testName");
    if (testName == null || testName.trim().isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/tests");
      return;
    }

    session.setAttribute(CURRENT_TEST_NAME_ATTR, testName.trim());
    session.setAttribute(
        CURRENT_TEST_STARTED_AT_ATTR,
        LocalDateTime.now(ZoneId.of("America/Mexico_City")));
    session.setAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR, Boolean.FALSE);
    session.setAttribute(
        CURRENT_PARTICIPANT_NAME_ATTR,
        "Participante " + (System.currentTimeMillis() % 1000));

    resp.sendRedirect(req.getContextPath() + "/terms");
  }
}
