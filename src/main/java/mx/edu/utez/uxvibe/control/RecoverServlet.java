package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import mx.edu.utez.uxvibe.service.EmailService;
import mx.edu.utez.uxvibe.service.PasswordResetStore;
import mx.edu.utez.uxvibe.service.UserStore;

@WebServlet(value = "/recover")
public class RecoverServlet extends HttpServlet {

  private static final String RECOVER_VIEW = "/WEB-INF/views/recuperar-contrasena.jsp";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String SUCCESS_MESSAGE_ATTR = "successMessage";
  private static final String EMAIL_PARAM = "email";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    forwardToRecover(req, resp, null, null, null);
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
        null,
        null
      );
      return;
    }

    email = email.trim();
    if (!email.contains("@") || !email.contains(".")) {
      forwardToRecover(req, resp, "Ingresa un email válido.", null, email);
      return;
    }

    // Process recovery token if account exists
    if (UserStore.getInstance().exists(email)) {
      String token = PasswordResetStore.getInstance().createToken(email);
      if (token != null) {
        String resetUrl = buildResetUrl(req, token);
        EmailService.sendPasswordResetLink(email, resetUrl);
      }
    }

    // Generic confirmation message for security
    String successMsg = "Si la dirección " + email + " está registrada en nuestra plataforma, recibirás un correo con el enlace para restablecer tu contraseña. Revisa tu bandeja de entrada y spam.";
    forwardToRecover(req, resp, null, successMsg, email);
  }

  private String buildResetUrl(HttpServletRequest req, String token) {
    String scheme = req.getScheme();
    String serverName = req.getServerName();
    int serverPort = req.getServerPort();
    String contextPath = req.getContextPath();

    StringBuilder url = new StringBuilder();
    url.append(scheme).append("://").append(serverName);

    if (("http".equalsIgnoreCase(scheme) && serverPort != 80) ||
        ("https".equalsIgnoreCase(scheme) && serverPort != 443)) {
      url.append(":").append(serverPort);
    }

    url.append(contextPath).append("/reset-password?token=").append(token);
    return url.toString();
  }

  private void forwardToRecover(
    HttpServletRequest req,
    HttpServletResponse resp,
    String errorMessage,
    String successMessage,
    String email
  ) throws ServletException, IOException {
    if (errorMessage != null) {
      req.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);
    }
    if (successMessage != null) {
      req.setAttribute(SUCCESS_MESSAGE_ATTR, successMessage);
    }
    if (email != null) {
      req.setAttribute("email", email);
    }
    req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
