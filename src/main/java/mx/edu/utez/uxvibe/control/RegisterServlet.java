package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.UserStore;

@WebServlet(value = "/register")
public class RegisterServlet extends HttpServlet {

  private static final String REGISTER_VIEW = "/WEB-INF/views/registro.jsp";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      try {
        resp.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "No se pudo mostrar la página de registro.");
      } catch (IOException ignored) {
        // Fall through since the response is already in a failed state.
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String fullName = req.getParameter("fullName");
    String email = req.getParameter("email");
    String password = req.getParameter("password");
    String confirmPassword = req.getParameter("confirmPassword");

    if (fullName == null ||
        fullName.trim().isEmpty() ||
        email == null ||
        email.trim().isEmpty() ||
        password == null ||
        password.trim().isEmpty() ||
        confirmPassword == null ||
        confirmPassword.trim().isEmpty()) {
      req.setAttribute(
          ERROR_MESSAGE_ATTR,
          "Completa todos los campos para crear tu cuenta.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
      return;
    }

    if (!email.contains("@") || !email.contains(".")) {
      req.setAttribute(ERROR_MESSAGE_ATTR, "Ingresa un email válido.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
      return;
    }

    if (password.length() < 8) {
      req.setAttribute(
          ERROR_MESSAGE_ATTR,
          "La contraseña debe tener al menos 8 caracteres.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
      return;
    }

    if (!password.equals(confirmPassword)) {
      req.setAttribute(ERROR_MESSAGE_ATTR, "Las contraseñas no coinciden.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
      return;
    }

    if (UserStore.getInstance().exists(email)) {
      req.setAttribute(ERROR_MESSAGE_ATTR, "Ese email ya está registrado.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
      return;
    }

    UserAccount account = new UserAccount();
    account.setFullName(fullName.trim());
    account.setEmail(email.trim());
    account.setPassword(password);

    if (UserStore.getInstance().register(account)) {
      HttpSession session = req.getSession();
      session.setAttribute(
          "flashSuccess",
          "Cuenta creada correctamente. Ahora puedes iniciar sesión.");
      try {
        resp.sendRedirect(req.getContextPath() + "/login");
      } catch (IOException e) {
        try {
          resp.sendError(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "No se pudo redirigir al inicio de sesión.");
        } catch (IOException ignored) {
          // Fall through since the response is already in a failed state.
        }
      }
    } else {
      req.setAttribute(
          ERROR_MESSAGE_ATTR,
          "No se pudo crear la cuenta en este momento.");
      repopulate(req, fullName, email);
      forwardToRegister(req, resp);
    }
  }

  private void repopulate(
      HttpServletRequest req,
      String fullName,
      String email) {
    req.setAttribute("fullName", fullName);
    req.setAttribute("email", email);
  }

  private void forwardToRegister(
      HttpServletRequest req,
      HttpServletResponse resp) throws ServletException, IOException {
    try {
      req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      try {
        resp.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "No se pudo mostrar la página de registro.");
      } catch (IOException ignored) {
        // Fall through since the response is already in a failed state.
      }
    }
  }
}
