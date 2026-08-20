package mx.edu.utez.uxvibe.control;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.TestStore;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(name = "DeleteTestServlet", urlPatterns = { "/delete-test" })
public class DeleteTestServlet extends HttpServlet {
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
    if (testName == null || testName.trim().isEmpty()) {
      resp.sendRedirect(req.getHeader("Referer") == null ? req.getContextPath() + "/tests" : req.getHeader("Referer"));
      return;
    }

    // delete participants and responses for test first
    ParticipantStore.getInstance().deleteByTest(account.getEmail(), testName);
    // delete test
    TestStore.getInstance().deleteTest(account.getEmail(), testName);

    resp.sendRedirect(req.getContextPath() + "/tests");
  }
}
