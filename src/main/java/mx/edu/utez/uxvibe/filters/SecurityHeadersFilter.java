package mx.edu.utez.uxvibe.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class SecurityHeadersFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (response instanceof HttpServletResponse) {
      HttpServletResponse res = (HttpServletResponse) response;

      // Prevent Clickjacking attacks
      res.setHeader("X-Frame-Options", "SAMEORIGIN");

      // Prevent MIME-sniffing
      res.setHeader("X-Content-Type-Options", "nosniff");

      // Cross-site scripting (XSS) filter
      res.setHeader("X-XSS-Protection", "1; mode=block");

      // Control referrer information
      res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

      // Permissions policy for microphone access
      res.setHeader("Permissions-Policy", "microphone=(self), camera=(), geolocation=()");
    }

    chain.doFilter(request, response);
  }
}
