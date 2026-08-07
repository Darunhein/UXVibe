package mx.edu.utez.uxvibe.control;

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

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    boolean participantSession = UserRole.PARTICIPANT.equals(account.getRole());
    String testName = (String) session.getAttribute(CURRENT_TEST_NAME_ATTR);
    if (testName == null || testName.trim().isEmpty()) {
      if (participantSession) {
        testName = "Participación general";
        session.setAttribute(CURRENT_TEST_NAME_ATTR, testName);
      } else {
        redirectTo(req, resp, "/tests");
        return;
      }
    }

    if (
      !Boolean.TRUE.equals(
        session.getAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR)
      )
    ) {
      LocalDateTime startedAt = (LocalDateTime) session.getAttribute(
        CURRENT_TEST_STARTED_AT_ATTR
      );
      ParticipantStore.getInstance().registerCompletion(
        account.getEmail(),
        testName,
        startedAt,
        resolveParticipantName(session)
      );
      session.setAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR, Boolean.TRUE);
    }

    if (participantSession) {
      session.removeAttribute(CURRENT_TEST_NAME_ATTR);
      session.removeAttribute(CURRENT_TEST_STARTED_AT_ATTR);
      session.removeAttribute(CURRENT_TEST_COMPLETION_RECORDED_ATTR);
      session.removeAttribute(CURRENT_PARTICIPANT_NAME_ATTR);
      redirectTo(req, resp, "/logout");
      return;
    }

    redirectTo(
      req,
      resp,
      "/participants?testName=" + encodeQueryValue(testName.trim())
    );
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
    Object participantName = session.getAttribute(
      CURRENT_PARTICIPANT_NAME_ATTR
    );
    if (
      participantName instanceof String &&
      !((String) participantName).trim().isEmpty()
    ) {
      return (String) participantName;
    }
    String fallbackName = "Participante " + (System.currentTimeMillis() % 1000);
    session.setAttribute(CURRENT_PARTICIPANT_NAME_ATTR, fallbackName);
    return fallbackName;
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
