package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/participant-identity")
public class ParticipantIdentityServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute("currentUser");
    String testName = (String) session.getAttribute("currentTestName");
    String participantName = req.getParameter("participantName");
    if (participantName != null) {
      participantName = participantName.trim();
    }
    if (participantName == null || participantName.isEmpty()) {
      participantName = "Participante";
    }

    // Save participant name in session
    session.setAttribute("currentParticipantName", participantName);

    // Iterate over provided parameters and store them as survey responses where
    // appropriate.
    java.util.Enumeration<String> params = req.getParameterNames();
    while (params.hasMoreElements()) {
      String name = params.nextElement();
      if ("participantName".equals(name) || "_csrf".equals(name)) {
        continue;
      }
      String[] values = req.getParameterValues(name);
      if (values == null || values.length == 0) {
        continue;
      }
      String value = values[0];

      // Age validation: must be numeric and >= 3 if provided
      if ("age".equals(name)) {
        try {
          int age = Integer.parseInt(value.trim());
          if (age < 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("La edad debe ser mayor o igual a 3 años.");
            return;
          }
          session.setAttribute("participantAge", age);
          ParticipantStore.getInstance().saveSurveyResponse(
              account.getEmail(),
              testName,
              participantName,
              "age",
              age);
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          resp.getWriter().write("Edad inválida.");
          return;
        }
        continue;
      }

      // For other fields we store raw value (strings) - ParticipantStore will compute
      // numeric equivalents.
      ParticipantStore.getInstance().saveSurveyResponse(
          account.getEmail(),
          testName,
          participantName,
          name,
          value);
    }

    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
