package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;
import mx.edu.utez.uxvibe.service.UserStore;

import java.io.IOException;

@WebServlet(value = "/register")
public class RegisterServlet extends HttpServlet {
    private static final String REGISTER_VIEW = "/WEB-INF/views/register.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("role", UserRole.EVALUATOR);
        req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String role = req.getParameter("role");

        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()
                || role == null || role.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Completa todos los campos para crear tu cuenta.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        if (!UserRole.isValid(role)) {
            req.setAttribute("errorMessage", "Selecciona un rol válido para crear tu cuenta.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            req.setAttribute("errorMessage", "Ingresa un email válido.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        if (password.length() < 8) {
            req.setAttribute("errorMessage", "La contraseña debe tener al menos 8 caracteres.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Las contraseñas no coinciden.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        if (UserStore.getInstance().exists(email)) {
            req.setAttribute("errorMessage", "Ese email ya está registrado.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
            return;
        }

        UserAccount account = new UserAccount();
        account.setFullName(fullName.trim());
        account.setEmail(email.trim());
        account.setPassword(password);
        account.setRole(role);

        if (UserStore.getInstance().register(account)) {
            HttpSession session = req.getSession();
            session.setAttribute("flashSuccess", "Cuenta creada correctamente. Ahora puedes iniciar sesión.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("errorMessage", "No se pudo crear la cuenta en este momento.");
            repopulate(req, fullName, email, role);
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
        }
    }

    private void repopulate(HttpServletRequest req, String fullName, String email, String role) {
        req.setAttribute("fullName", fullName);
        req.setAttribute("email", email);
        req.setAttribute("role", UserRole.normalize(role));
    }
}
