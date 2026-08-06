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

@WebServlet(value = "/login")
public class LoginServlet extends HttpServlet {
    private static final String LOGIN_VIEW = "/WEB-INF/views/login.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") instanceof UserAccount) {
            UserAccount currentUser = (UserAccount) session.getAttribute("currentUser");
            resp.sendRedirect(req.getContextPath() + resolveHomePath(currentUser));
            return;
        }

        req.setAttribute("successMessage", consumeFlash(req, "flashSuccess"));
        req.setAttribute("errorMessage", consumeFlash(req, "flashError"));
        req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            req.setAttribute("email", email);
            req.setAttribute("errorMessage", "Ingresa tu email y contraseña para continuar.");
            req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
            return;
        }

        UserAccount account = UserStore.getInstance().authenticate(email, password);
        if (account == null) {
            req.setAttribute("email", email);
            req.setAttribute("errorMessage", "Email o contraseña incorrectos.");
            req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("currentUser", account);
        resp.sendRedirect(req.getContextPath() + resolveHomePath(account));
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
