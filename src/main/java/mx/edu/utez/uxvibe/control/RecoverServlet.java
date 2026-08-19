package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import mx.edu.utez.uxvibe.service.EmailService;
import mx.edu.utez.uxvibe.service.UserStore;

@WebServlet(value = "/recover")
public class RecoverServlet extends HttpServlet {

  private static final String RECOVER_VIEW = "/WEB-INF/views/recuperar-contrasena.jsp";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String SUCCESS_MESSAGE_ATTR = "successMessage";
  private static final String FLASH_SUCCESS_ATTR = "flashSuccess";
  private static final String EMAIL_PARAM = "email";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    forwardToRecover(req, resp, null, null);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String email = req.getParameter(EMAIL_PARAM);
    if (isBlank(email)) {
      forwardToRecover(
        req,
        resp,
        "Ingresa tu email para recuperar tu contraseña.",
        null
      );
      return;
    }

    email = email.trim();
    if (!email.contains("@") || !email.contains(".")) {
      forwardToRecover(req, resp, "Ingresa un email válido.", email);
      return;
    }

    if (!UserStore.getInstance().exists(email)) {
      forwardToRecover(
        req,
        resp,
        "No encontramos una cuenta asociada a ese email.",
        email
      );
      return;
    }

    // Generate a new temporary password (e.g. 8 characters)
    String newPassword = generateRandomPassword(8);
    boolean updated = UserStore.getInstance().resetPassword(email, newPassword);

    if (!updated) {
      forwardToRecover(
        req,
        resp,
        "No fue posible restablecer la contraseña. Intenta de nuevo más tarde.",
        email
      );
      return;
    }

    boolean emailSent = EmailService.sendPasswordResetEmail(email, newPassword);

    HttpSession session = req.getSession();
    if (emailSent) {
      session.setAttribute(
        FLASH_SUCCESS_ATTR,
        "Te hemos enviado tu nueva contraseña a " + email + ". Inicia sesión con ella."
      );
    } else {
      session.setAttribute(
        FLASH_SUCCESS_ATTR,
        "Se generó tu nueva contraseña: " + newPassword + " (Copia y pégala para iniciar sesión)."
      );
    }

    redirectToLogin(req, resp);
  }

  private void forwardToRecover(
    HttpServletRequest req,
    HttpServletResponse resp,
    String errorMessage,
    String email
  ) throws ServletException, IOException {
    if (errorMessage != null) {
      req.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);
    }
    if (email != null) {
      req.setAttribute("email", email);
    }
    req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
  }

  private void redirectToLogin(
    HttpServletRequest req,
    HttpServletResponse resp
  ) throws IOException {
    resp.sendRedirect(req.getContextPath() + "/login");
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private String generateRandomPassword(int length) {
    final String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    SecureRandom rnd = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(rnd.nextInt(chars.length())));
    }
    return sb.toString();
  }
}
