package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.TestStore;

import java.io.IOException;

@WebServlet(value = "/create-test")
public class CreateTestServlet extends HttpServlet {
    private static final String CREATE_TEST_VIEW = "/WEB-INF/views/create-test.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.getRequestDispatcher(CREATE_TEST_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String testName = req.getParameter("testName");
        if (testName == null || testName.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Ingresa el nombre de la prueba.");
            req.setAttribute("testName", testName);
            req.getRequestDispatcher(CREATE_TEST_VIEW).forward(req, resp);
            return;
        }

        UserAccount account = (UserAccount) session.getAttribute("currentUser");
        TestStore.getInstance().createTest(account.getEmail(), testName);
        resp.sendRedirect(req.getContextPath() + "/tests");
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }
}
