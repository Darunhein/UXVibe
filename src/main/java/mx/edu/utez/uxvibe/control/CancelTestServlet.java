package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;

@WebServlet(value = "/cancel-test")
public class CancelTestServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Cancela la prueba desde el menú de grabación.");
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    String role = null;
    if (session != null) {
      Object currentUserAttribute = session.getAttribute("currentUser");
      if (currentUserAttribute instanceof UserAccount) {
        role = ((UserAccount) currentUserAttribute).getRole();
      }
      session.removeAttribute("currentTestName");
      session.removeAttribute("currentTestStartedAt");
      session.removeAttribute("currentTestCompletionRecorded");
    }

    if (UserRole.PARTICIPANT.equals(role)) {
      redirectTo(req, resp, "/logout");
      return;
    }

    redirectTo(req, resp, "/tests");
  }

  private void redirectTo(
      HttpServletRequest req,
      HttpServletResponse resp,
      String path) throws IOException {
    try {
      resp.sendRedirect(req.getContextPath() + path);
    } catch (IOException e) {
      resp.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "No se pudo redirigir a la ruta solicitada.");
    }
  }
}
