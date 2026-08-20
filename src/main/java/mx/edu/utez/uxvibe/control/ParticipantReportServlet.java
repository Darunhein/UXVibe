package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.bean.ParticipantReportBean;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/participant-report")
public class ParticipantReportServlet extends HttpServlet {

  private static final String REPORT_VIEW = "/WEB-INF/views/reporte-participante.jsp";
  private static final String CURRENT_USER_ATTR = "currentUser";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    String testName = req.getParameter("testName");
    String participantName = req.getParameter("participantName");

    ParticipantReportBean report = ParticipantStore.getInstance().getReport(
        account.getEmail(),
        testName,
        participantName);
    if (report == null) {
      report = new ParticipantReportBean();
      report.setParticipantName(
          participantName == null ? "Participante" : participantName);
      report.setTestName(testName == null ? "Prueba sin nombre" : testName);
      report.setDescription(
          "No se encontró información adicional para este participante.");
    }

    req.setAttribute("report", report);
    req.getRequestDispatcher(REPORT_VIEW).forward(req, resp);
  }
}
