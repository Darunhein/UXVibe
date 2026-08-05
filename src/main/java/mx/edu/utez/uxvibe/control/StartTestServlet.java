package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(value = "/start-test")
public class StartTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String testName = req.getParameter("testName");
        if (testName == null || testName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        session.setAttribute("currentTestName", testName.trim());
        session.setAttribute("currentTestStartedAt", LocalDateTime.now());
        session.setAttribute("currentTestCompletionRecorded", Boolean.FALSE);
        resp.sendRedirect(req.getContextPath() + "/html/03%20Execution%20Line/terminos-y-condiciones.jsp");
    }
}
