package mx.edu.utez.uxvibe.control;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(name = "DeleteParticipantServlet", urlPatterns = {"/delete-participant"})
public class DeleteParticipantServlet extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }
    UserAccount account = (UserAccount) session.getAttribute("currentUser");

    String testName = req.getParameter("testName");
    String participantName = req.getParameter("participantName");
    if (testName == null || testName.trim().isEmpty() || participantName == null || participantName.trim().isEmpty()) {
      resp.sendRedirect(req.getHeader("Referer") == null ? req.getContextPath() + "/participants" : req.getHeader("Referer"));
      return;
    }

    ParticipantStore.getInstance().deleteParticipant(account.getEmail(), testName, participantName);

    resp.sendRedirect(req.getContextPath() + "/participants?testName=" + java.net.URLEncoder.encode(testName, java.nio.charset.StandardCharsets.UTF_8));
  }
}
