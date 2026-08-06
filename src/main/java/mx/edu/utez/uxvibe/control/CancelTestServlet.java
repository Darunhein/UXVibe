package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;

import java.io.IOException;

@WebServlet(value = "/cancel-test")
public class CancelTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String role = null;
        if (session != null) {
            Object currentUserAttribute = session.getAttribute("currentUser");
            if (currentUserAttribute instanceof UserAccount) {
                role = ((UserAccount) currentUserAttribute).getRole();
            }
            session.removeAttribute("currentTestName");
            session.removeAttribute("currentTestStartedAt");
            session.removeAttribute("currentTestCompletionRecorded");
        }

        if (UserRole.PARTICIPANT.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/tests");
    }
}
