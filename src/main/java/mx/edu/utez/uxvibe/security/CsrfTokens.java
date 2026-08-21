package mx.edu.utez.uxvibe.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokens {
  public static final String SESSION_ATTR = "csrfToken";
  public static final String PARAM_NAME = "_csrf";
  public static final String HEADER_NAME = "X-CSRF-Token";

  private static final SecureRandom RANDOM = new SecureRandom();

  private CsrfTokens() {
  }

  public static String get(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    Object existing = session.getAttribute(SESSION_ATTR);
    if (existing instanceof String && !((String) existing).isBlank()) {
      return (String) existing;
    }
    byte[] bytes = new byte[32];
    RANDOM.nextBytes(bytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    session.setAttribute(SESSION_ATTR, token);
    return token;
  }

  public static boolean isValid(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return false;
    }
    Object expected = session.getAttribute(SESSION_ATTR);
    if (!(expected instanceof String) || ((String) expected).isBlank()) {
      return false;
    }
    String provided = request.getHeader(HEADER_NAME);
    if (provided == null || provided.isBlank()) {
      provided = request.getParameter(PARAM_NAME);
    }
    if (provided == null || provided.isBlank()) {
      return false;
    }
    return constantTimeEquals((String) expected, provided);
  }

  private static boolean constantTimeEquals(String left, String right) {
    byte[] a = left.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    byte[] b = right.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    if (a.length != b.length) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < a.length; i++) {
      result |= a[i] ^ b[i];
    }
    return result == 0;
  }
}
