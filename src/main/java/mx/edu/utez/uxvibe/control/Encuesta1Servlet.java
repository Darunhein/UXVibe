package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/encuesta-1")
public class Encuesta1Servlet extends HttpServlet {

  private static final String VIEW = "/WEB-INF/views/encuesta-1.jsp";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    req.getRequestDispatcher(VIEW).forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute("currentUser");
    String testName = (String) session.getAttribute("currentTestName");
    String participantName = (String) session.getAttribute("currentParticipantName");
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
      String value = req.getParameter(name);
      if (value != null && !value.trim().isEmpty()) {
        ParticipantStore.getInstance().saveSurveyResponse(
            account.getEmail(),
            testName,
            participantName,
            name,
            value.trim());
      }
    }

    resp.sendRedirect(req.getContextPath() + "/encuesta-2");
  }
}
