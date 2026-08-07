package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;

@WebServlet(value = "/start-test")
public class StartTestServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      redirectTo(resp, req.getContextPath() + "/login");
      return;
    }

    String testName = req.getParameter("testName");
    if (testName == null || testName.trim().isEmpty()) {
      redirectTo(resp, req.getContextPath() + "/tests");
      return;
    }

    session.setAttribute("currentTestName", testName.trim());
    session.setAttribute(
      "currentTestStartedAt",
      LocalDateTime.now(ZoneId.systemDefault())
    );
    session.setAttribute("currentTestCompletionRecorded", Boolean.FALSE);
    redirectTo(
      resp,
      req.getContextPath() +
        "/html/03%20Execution%20Line/terminos-y-condiciones.jsp"
    );
  }

  private void redirectTo(HttpServletResponse resp, String location) {
    resp.setStatus(HttpServletResponse.SC_FOUND);
    resp.setHeader("Location", location);
  }
}
