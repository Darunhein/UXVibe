package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.uxvibe.model.ParticipantItem;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(value = "/participants")
public class ParticipantsServlet extends HttpServlet {
    private static final String PARTICIPANTS_VIEW = "/WEB-INF/views/participants.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount account = (UserAccount) session.getAttribute("currentUser");
        String selectedTestName = resolveTestName(req.getParameter("testName"), (String) session.getAttribute("currentTestName"));
        if (!selectedTestName.isEmpty()) {
            session.setAttribute("currentTestName", selectedTestName);
        }

        List<ParticipantItem> allParticipants = ParticipantStore.getInstance()
                .listByUserAndTest(account.getEmail(), selectedTestName);
        int totalParticipants = allParticipants.size();
        int totalPages = PaginationSupport.countPages(totalParticipants, PaginationSupport.PAGE_SIZE);
        int currentPage = PaginationSupport.resolvePage(req.getParameter("page"), totalPages);

        req.setAttribute("participants", PaginationSupport.paginate(allParticipants, currentPage, PaginationSupport.PAGE_SIZE));
        req.setAttribute("selectedTestName", selectedTestName);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("pageHrefPrefix", buildPageHrefPrefix(req, selectedTestName));
        req.getRequestDispatcher(PARTICIPANTS_VIEW).forward(req, resp);
    }

    private String resolveTestName(String parameterValue, String sessionValue) {
        String resolved = normalize(parameterValue);
        if (!resolved.isEmpty()) {
            return resolved;
        }
        return normalize(sessionValue);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String buildPageHrefPrefix(HttpServletRequest req, String selectedTestName) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(req.getContextPath()).append("/participants?");
        if (!selectedTestName.isEmpty()) {
            builder.append("testName=").append(URLEncoder.encode(selectedTestName, "UTF-8")).append("&");
        }
        builder.append("page=");
        return builder.toString();
    }
}
