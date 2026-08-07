package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;
import mx.edu.utez.uxvibe.service.UserStore;

@WebServlet(value = "/login")
public class LoginServlet extends HttpServlet {

  private static final String LOGIN_VIEW = "/WEB-INF/views/login.jsp";
  private static final String CURRENT_USER_ATTR = "currentUser";
  private static final String FLASH_SUCCESS_ATTR = "flashSuccess";
  private static final String FLASH_ERROR_ATTR = "flashError";
  private static final String SUCCESS_MESSAGE_ATTR = "successMessage";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String EMAIL_ATTR = "email";
  private static final String EMAIL_PARAM = "email";
  private static final String PASSWORD_PARAM = "password";
  private static final String LOGIN_ERROR_MESSAGE =
    "No se pudo mostrar la página de inicio de sesión.";
  private static final String REDIRECT_ERROR_MESSAGE =
    "No se pudo redirigir al inicio correcto.";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (
      session != null &&
      session.getAttribute(CURRENT_USER_ATTR) instanceof UserAccount
    ) {
      UserAccount currentUser = (UserAccount) session.getAttribute(
        CURRENT_USER_ATTR
      );
      redirectToHome(req, resp, currentUser);
      return;
    }

    req.setAttribute(
      SUCCESS_MESSAGE_ATTR,
      consumeFlash(req, FLASH_SUCCESS_ATTR)
    );
    req.setAttribute(ERROR_MESSAGE_ATTR, consumeFlash(req, FLASH_ERROR_ATTR));
    forwardToLogin(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String email = req.getParameter(EMAIL_PARAM);
    String password = req.getParameter(PASSWORD_PARAM);

    if (isBlank(email) || isBlank(password)) {
      forwardToLogin(
        req,
        resp,
        "Ingresa tu email y contraseña para continuar.",
        email
      );
      return;
    }

    UserAccount account = UserStore.getInstance().authenticate(email, password);
    if (account == null) {
      forwardToLogin(req, resp, "Email o contraseña incorrectos.", email);
      return;
    }

    HttpSession session = req.getSession();
    session.setAttribute(CURRENT_USER_ATTR, account);
    redirectToHome(req, resp, account);
  }

  private void forwardToLogin(
    HttpServletRequest req,
    HttpServletResponse resp
  ) {
    forwardToLogin(req, resp, null, null);
  }

  private void forwardToLogin(
    HttpServletRequest req,
    HttpServletResponse resp,
    String errorMessage,
    String email
  ) {
    if (errorMessage != null) {
      req.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);
    }
    if (email != null) {
      req.setAttribute(EMAIL_ATTR, email);
    }

    try {
      req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      writeError(resp, LOGIN_ERROR_MESSAGE);
    }
  }

  private void redirectToHome(
    HttpServletRequest req,
    HttpServletResponse resp,
    UserAccount account
  ) {
    try {
      resp.sendRedirect(req.getContextPath() + resolveHomePath(account));
    } catch (IOException e) {
      writeError(resp, REDIRECT_ERROR_MESSAGE);
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

  private String consumeFlash(HttpServletRequest req, String attributeName) {
    HttpSession session = req.getSession(false);
    if (session == null) {
      return null;
    }
    Object value = session.getAttribute(attributeName);
    if (value != null) {
      session.removeAttribute(attributeName);
    }
    return value == null ? null : String.valueOf(value);
  }

  private String resolveHomePath(UserAccount account) {
    if (account != null && UserRole.PARTICIPANT.equals(account.getRole())) {
      return "/html/03%20Execution%20Line/terminos-y-condiciones.jsp";
    }
    return "/tests";
  }
}
