package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/survey-submit")
public class SurveySubmissionServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute("currentUser");
    String testName = (String) session.getAttribute("currentTestName");
    String participantName = (String) session.getAttribute(
        "currentParticipantName");
    if (participantName == null || participantName.trim().isEmpty()) {
      participantName = "Participante " + (System.currentTimeMillis() % 1000);
      session.setAttribute("currentParticipantName", participantName);
    }

    Enumeration<String> parameterNames = req.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String name = parameterNames.nextElement();
      if ("submit".equalsIgnoreCase(name)) {
        continue;
      }
      String[] values = req.getParameterValues(name);
      if (values != null && values.length > 0) {
        ParticipantStore.getInstance().saveSurveyResponse(
            account.getEmail(),
            testName,
            participantName,
            name,
            values[0]);
      }
    }

    resp.sendRedirect(req.getContextPath() + "/cheers-bye");
  }
}
