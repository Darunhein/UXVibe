package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.service.UserStore;

import java.io.IOException;

@WebServlet(value = "/recover")
public class RecoverServlet extends HttpServlet {
    private static final String RECOVER_VIEW = "/WEB-INF/views/recover.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Ingresa tu email para recuperar tu contraseña.");
            req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            req.setAttribute("errorMessage", "Ingresa un email válido.");
            req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
            return;
        }

        if (!UserStore.getInstance().exists(email)) {
            req.setAttribute("errorMessage", "No encontramos una cuenta asociada a ese email.");
            req.getRequestDispatcher(RECOVER_VIEW).forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("flashSuccess", "Te enviamos un enlace de recuperación a " + email);
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
