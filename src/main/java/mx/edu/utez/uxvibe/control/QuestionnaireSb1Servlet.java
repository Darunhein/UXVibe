package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/cuestionario-sb-1")
public class QuestionnaireSb1Servlet extends HttpServlet {

  private static final String VIEW = "/WEB-INF/views/cuestionario-sb-1.jsp";

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
    String fullName = req.getParameter("fullName");
    String ageStr = req.getParameter("age");
    String gender = req.getParameter("gender");
    String education = req.getParameter("education");

    if (fullName != null && !fullName.trim().isEmpty()) {
      fullName = fullName.trim();
      session.setAttribute("currentParticipantName", fullName);
    } else {
      fullName = (String) session.getAttribute("currentParticipantName");
      if (fullName == null || fullName.trim().isEmpty()) {
        fullName = "Participante " + (System.currentTimeMillis() % 1000);
        session.setAttribute("currentParticipantName", fullName);
      }
    }

    if (ageStr != null && !ageStr.trim().isEmpty()) {
      try {
        int age = Integer.parseInt(ageStr.trim());
        ParticipantStore.getInstance().saveSurveyResponse(account.getEmail(), testName, fullName, "age", age);
      } catch (NumberFormatException ignored) {}
    }

    if (gender != null && !gender.trim().isEmpty()) {
      ParticipantStore.getInstance().saveSurveyResponse(account.getEmail(), testName, fullName, "gender", gender.trim());
    }

    if (education != null && !education.trim().isEmpty()) {
      ParticipantStore.getInstance().saveSurveyResponse(account.getEmail(), testName, fullName, "education", education.trim());
    }

    resp.sendRedirect(req.getContextPath() + "/cuestionario-sb-2");
  }
}
