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
import java.time.LocalDateTime;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/complete-test")
public class CompleteTestServlet extends HttpServlet {

  private static final String CURRENT_USER_ATTR = "currentUser";
  private static final String CURRENT_TEST_NAME_ATTR = "currentTestName";
  private static final String CURRENT_TEST_STARTED_AT_ATTR = "currentTestStartedAt";
  private static final String CURRENT_TEST_COMPLETION_RECORDED_ATTR = "currentTestCompletionRecorded";
  private static final String CURRENT_PARTICIPANT_NAME_ATTR = "currentParticipantName";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Completar la prueba requiere confirmar desde el formulario.");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    processCompletion(req, resp);
  }

  private void processCompletion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      redirectTo(req, resp, "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    boolean participantSession = UserRole.PARTICIPANT.equals(account.getRole());

    String paramTestName = req.getParameter("testName");
    String sessionTestName = (String) session.getAttribute(CURRENT_TEST_NAME_ATTR);
    String testName = (paramTestName != null && !paramTestName.trim().isEmpty()) ? paramTestName.trim()
        : sessionTestName;

    if (testName == null || testName.trim().isEmpty()) {
      if (participantSession) {
        testName = "Participación general";
        session.setAttribute(CURRENT_TEST_NAME_ATTR, testName);
      } else {
        redirectTo(req, resp, "/tests");
        return;
      }
    }

    String paramParticipantName = req.getParameter("participantName");
    String participantName = (paramParticipantName != null && !paramParticipantName.trim().isEmpty())
        ? paramParticipantName.trim()
        : resolveParticipantName(session);

    LocalDateTime startedAt = (LocalDateTime) session.getAttribute(CURRENT_TEST_STARTED_AT_ATTR);
    ParticipantStore.getInstance().registerCompletion(
        account.getEmail(),
        testName,
        startedAt,
        participantName);
    session.setAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR, Boolean.TRUE);

    if (participantSession) {
      session.removeAttribute(CURRENT_TEST_NAME_ATTR);
      session.removeAttribute(CURRENT_TEST_STARTED_AT_ATTR);
      session.removeAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR);
      session.removeAttribute(CURRENT_PARTICIPANT_NAME_ATTR);
      redirectTo(req, resp, "/logout");
      return;
    }

    // Redirect to participant report so the evaluator can immediately assess the
    // completed test
    redirectTo(
        req,
        resp,
        "/participant-report?testName=" + encodeQueryValue(testName.trim()) +
            "&participantName=" + encodeQueryValue(participantName.trim()));
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

  private String resolveParticipantName(HttpSession session) {
    Object participantName = session.getAttribute(CURRENT_PARTICIPANT_NAME_ATTR);
    if (participantName instanceof String && !((String) participantName).trim().isEmpty()) {
      return ((String) participantName).trim();
    }
    String fallbackName = mx.edu.utez.uxvibe.util.ParticipantIds.newFallbackName();
    session.setAttribute(CURRENT_PARTICIPANT_NAME_ATTR, fallbackName);
    return fallbackName;
  }

  private void redirectTo(
      HttpServletRequest req,
      HttpServletResponse resp,
      String path) throws IOException {
    resp.sendRedirect(req.getContextPath() + path);
  }
}
