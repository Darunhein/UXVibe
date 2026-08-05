package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(value = "/cancel-test")
public class CancelTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.removeAttribute("currentTestName");
            session.removeAttribute("currentTestStartedAt");
            session.removeAttribute("currentTestCompletionRecorded");
        }

        resp.sendRedirect(req.getContextPath() + "/tests");
    }
}
