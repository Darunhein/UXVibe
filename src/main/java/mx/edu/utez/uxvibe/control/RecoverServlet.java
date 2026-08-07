package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.service.UserStore;

@WebServlet(value = "/recover")
public class RecoverServlet extends HttpServlet {

  private static final String RECOVER_VIEW = "/WEB-INF/views/recover.jsp";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String FLASH_SUCCESS_ATTR = "flashSuccess";
  private static final String EMAIL_PARAM = "email";
  private static final String RECOVER_ERROR_MESSAGE =
    "No se pudo volver a mostrar la página de recuperación.";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    forwardToRecover(req, resp, null);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String email = req.getParameter(EMAIL_PARAM);
    if (isBlank(email)) {
      forwardToRecover(
        req,
        resp,
        "Ingresa tu email para recuperar tu contraseña."
      );
      return;
    }

    if (!email.contains("@") || !email.contains(".")) {
      forwardToRecover(req, resp, "Ingresa un email válido.");
      return;
    }

    if (!UserStore.getInstance().exists(email)) {
      forwardToRecover(
        req,
        resp,
        "No encontramos una cuenta asociada a ese email."
      );
      return;
    }

    HttpSession session = req.getSession();
    session.setAttribute(
      FLASH_SUCCESS_ATTR,
      "Te enviamos un enlace de recuperación a " + email
    );
    redirectToLogin(req, resp);
  }

  private void forwardToRecover(
    HttpServletRequest req,
    HttpServletResponse resp,
    String message
  ) {
    if (message != null) {
      req.setAttribute(ERROR_MESSAGE_ATTR, message);
    }

    try {
      req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      writeError(resp, RECOVER_ERROR_MESSAGE);
    }
  }

  private void redirectToLogin(
    HttpServletRequest req,
    HttpServletResponse resp
  ) {
    try {
      resp.sendRedirect(req.getContextPath() + "/login");
    } catch (IOException e) {
      writeError(resp, "No se pudo redirigir al inicio de sesión.");
    }
  }

  private void writeError(HttpServletResponse resp, String message) {
    try {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    } catch (IOException ignored) {
      // Fall through since the response is already in a failed state.
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
