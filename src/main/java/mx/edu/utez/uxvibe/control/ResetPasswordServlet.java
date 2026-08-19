package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.PasswordResetToken;
import mx.edu.utez.uxvibe.service.PasswordResetStore;

@WebServlet(value = "/reset-password")
public class ResetPasswordServlet extends HttpServlet {

  private static final String RESET_VIEW = "/WEB-INF/views/cambiar-contrasena.jsp";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String VALID_TOKEN_ATTR = "validToken";
  private static final String TOKEN_ATTR = "token";
  private static final String FLASH_SUCCESS_ATTR = "flashSuccess";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String token = req.getParameter("token");

    if (isBlank(token)) {
      req.setAttribute(VALID_TOKEN_ATTR, false);
      req.setAttribute(
        ERROR_MESSAGE_ATTR,
        "No se proporcionó un token de recuperación. Solicita un nuevo enlace."
      );
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    PasswordResetToken resetToken = PasswordResetStore.getInstance().validateToken(token);
    if (resetToken == null) {
      req.setAttribute(VALID_TOKEN_ATTR, false);
      req.setAttribute(
        ERROR_MESSAGE_ATTR,
        "El enlace de recuperación es inválido o ha expirado. Por favor, solicita uno nuevo."
      );
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    req.setAttribute(VALID_TOKEN_ATTR, true);
    req.setAttribute(TOKEN_ATTR, token.trim());
    req.setAttribute("email", resetToken.getEmail());
    req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    String token = req.getParameter("token");
    String password = req.getParameter("password");
    String confirmPassword = req.getParameter("confirmPassword");

    if (isBlank(token)) {
      req.setAttribute(VALID_TOKEN_ATTR, false);
      req.setAttribute(
        ERROR_MESSAGE_ATTR,
        "El token de recuperación no es válido. Solicita un nuevo enlace."
      );
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    PasswordResetToken resetToken = PasswordResetStore.getInstance().validateToken(token);
    if (resetToken == null) {
      req.setAttribute(VALID_TOKEN_ATTR, false);
      req.setAttribute(
        ERROR_MESSAGE_ATTR,
        "El enlace de recuperación ha expirado o ya fue utilizado. Solicita uno nuevo."
      );
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    if (isBlank(password) || isBlank(confirmPassword)) {
      req.setAttribute(VALID_TOKEN_ATTR, true);
      req.setAttribute(TOKEN_ATTR, token);
      req.setAttribute(ERROR_MESSAGE_ATTR, "Por favor, completa todos los campos.");
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    if (password.length() < 8) {
      req.setAttribute(VALID_TOKEN_ATTR, true);
      req.setAttribute(TOKEN_ATTR, token);
      req.setAttribute(ERROR_MESSAGE_ATTR, "La nueva contraseña debe tener al menos 8 caracteres.");
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    if (!password.equals(confirmPassword)) {
      req.setAttribute(VALID_TOKEN_ATTR, true);
      req.setAttribute(TOKEN_ATTR, token);
      req.setAttribute(ERROR_MESSAGE_ATTR, "Las contraseñas no coinciden.");
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    boolean success = PasswordResetStore.getInstance().resetPasswordWithToken(token, password);
    if (!success) {
      req.setAttribute(VALID_TOKEN_ATTR, true);
      req.setAttribute(TOKEN_ATTR, token);
      req.setAttribute(
        ERROR_MESSAGE_ATTR,
        "No fue posible restablecer la contraseña en este momento. Intenta de nuevo."
      );
      req.getRequestDispatcher(RESET_VIEW).forward(req, resp);
      return;
    }

    HttpSession session = req.getSession();
    session.setAttribute(
      FLASH_SUCCESS_ATTR,
      "¡Tu contraseña ha sido restablecida exitosamente! Inicia sesión con tu nueva contraseña."
    );
    resp.sendRedirect(req.getContextPath() + "/login");
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
