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

@WebServlet(value = "/cheers-bye")
public class CheersByeServlet extends HttpServlet {

  private static final String VIEW = "/WEB-INF/views/cheers-bye.jsp";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute("currentUser");
    String testName = (String) session.getAttribute("currentTestName");
    String participantName = (String) session.getAttribute("currentParticipantName");

    ParticipantReportBean report = ParticipantStore.getInstance().getReport(
      account.getEmail(),
      testName,
      participantName
    );

    req.setAttribute("report", report);
    req.setAttribute("testName", testName);
    req.setAttribute("participantName", participantName);

    req.getRequestDispatcher(VIEW).forward(req, resp);
  }
}
