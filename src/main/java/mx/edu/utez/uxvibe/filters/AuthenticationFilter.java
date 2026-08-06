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
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/tests",
        "/participants",
        "/create-test",
        "/start-test",
        "/complete-test",
        "/cancel-test",
        "/restart-recording",
        "/html/03 Execution Line/*",
        "/html/04 SAM Line/*"
})
public class AuthenticationFilter implements Filter {
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
        if (!UserRole.isValid(currentUser.getRole())) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
