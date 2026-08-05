package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@WebServlet(value = "/complete-test")
public class CompleteTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String testName = (String) session.getAttribute("currentTestName");
        if (testName == null || testName.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/tests");
            return;
        }

        if (!Boolean.TRUE.equals(session.getAttribute("currentTestCompletionRecorded"))) {
            UserAccount account = (UserAccount) session.getAttribute("currentUser");
            LocalDateTime startedAt = (LocalDateTime) session.getAttribute("currentTestStartedAt");
            ParticipantStore.getInstance().registerCompletion(account.getEmail(), testName, startedAt);
            session.setAttribute("currentTestCompletionRecorded", Boolean.TRUE);
        }

        resp.sendRedirect(
                req.getContextPath() + "/participants?testName=" + URLEncoder.encode(testName.trim(), "UTF-8")
        );
    }
}
