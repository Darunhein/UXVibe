package mx.edu.utez.uxvibe.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import mx.edu.utez.uxvibe.security.CsrfTokens;

@WebFilter(urlPatterns = "/*")
public class CsrfFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    if (!"POST".equalsIgnoreCase(req.getMethod()) || isStaticPath(req.getServletPath())) {
      chain.doFilter(request, response);
      return;
    }

    if (!CsrfTokens.isValid(req)) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Solicitud no válida (CSRF). Recarga la página e inténtalo de nuevo.");
      return;
    }

    chain.doFilter(request, response);
  }

  private boolean isStaticPath(String servletPath) {
    if (servletPath == null) {
      return false;
    }
    return servletPath.startsWith("/CSS/")
        || servletPath.startsWith("/JavaScript/")
        || servletPath.startsWith("/public/");
  }
}
