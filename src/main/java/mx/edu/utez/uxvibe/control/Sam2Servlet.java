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

@WebServlet(value = "/sam-2")
public class Sam2Servlet extends HttpServlet {

  private static final String VIEW = "/WEB-INF/views/sam-2.jsp";

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

    String impact = req.getParameter("impact");
    if (impact != null && !impact.trim().isEmpty()) {
      ParticipantStore.getInstance().saveSurveyResponse(account.getEmail(), testName, participantName, "impact", impact.trim());
    }

    resp.sendRedirect(req.getContextPath() + "/sam-3");
  }
}
