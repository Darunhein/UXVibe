package mx.edu.utez.uxvibe.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;

@WebFilter(urlPatterns = {
    "/tests",
    "/participants",
    "/create-test",
    "/start-test",
    "/complete-test",
    "/cancel-test",
    "/restart-recording",
    "/participant-report"
})
public class RoleAuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Object currentUserAttribute = session.getAttribute("currentUser");
        if (!(currentUserAttribute instanceof UserAccount)) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount currentUser = (UserAccount) currentUserAttribute;
        String role = currentUser.getRole();
        if (!UserRole.isValid(role)) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String servletPath = req.getServletPath();
        if (UserRole.PARTICIPANT.equals(role) && isParticipantBlockedRoute(servletPath)) {
            resp.sendRedirect(req.getContextPath() + "/terms");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isParticipantBlockedRoute(String servletPath) {
        return "/tests".equals(servletPath)
                || "/participants".equals(servletPath)
                || "/create-test".equals(servletPath)
                || "/start-test".equals(servletPath)
                || "/participant-report".equals(servletPath);
    }
}
