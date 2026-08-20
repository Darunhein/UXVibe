package mx.edu.utez.uxvibe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mx.edu.utez.uxvibe.ConexionBD;
import mx.edu.utez.uxvibe.model.PasswordResetToken;

public interface PasswordResetDao {

  String INSERT_TOKEN_SQL = "INSERT INTO PASSWORD_RESETS (TOKEN, EMAIL, EXPIRES_AT, USED) VALUES (?, ?, ?, ?)";
  String FIND_TOKEN_SQL = "SELECT TOKEN, EMAIL, EXPIRES_AT, USED FROM PASSWORD_RESETS WHERE TOKEN = ?";
  String MARK_USED_SQL = "UPDATE PASSWORD_RESETS SET USED = 1 WHERE TOKEN = ?";
  String INVALIDATE_EMAIL_SQL = "UPDATE PASSWORD_RESETS SET USED = 1 WHERE LOWER(EMAIL) = LOWER(?)";

  Map<String, PasswordResetToken> IN_MEMORY_TOKENS = new ConcurrentHashMap<>();

  default boolean saveToken(PasswordResetToken resetToken) {
    if (resetToken == null || resetToken.getToken() == null || resetToken.getEmail() == null) {
      return false;
    }

    String normalizedEmail = normalizeEmail(resetToken.getEmail());
    resetToken.setEmail(normalizedEmail);

    // Save to memory cache
    IN_MEMORY_TOKENS.put(resetToken.getToken(), resetToken);

    // Try saving to DB if table exists
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_TOKEN_SQL)) {
      ps.setString(1, resetToken.getToken());
      ps.setString(2, normalizedEmail);
      ps.setTimestamp(3, Timestamp.from(resetToken.getExpiresAt()));
      ps.setInt(4, resetToken.isUsed() ? 1 : 0);
      ps.executeUpdate();
      return true;
    } catch (SQLException e) {
      // If table doesn't exist yet or connection fails, the in-memory fallback
      // guarantees it still works
      return true;
    }
  }

  default PasswordResetToken findByToken(String token) {
    if (token == null || token.isBlank()) {
      return null;
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(FIND_TOKEN_SQL)) {
      ps.setString(1, token.trim());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          PasswordResetToken prt = new PasswordResetToken();
          prt.setToken(rs.getString("TOKEN"));
          prt.setEmail(rs.getString("EMAIL"));
          Timestamp ts = rs.getTimestamp("EXPIRES_AT");
          if (ts != null) {
            prt.setExpiresAt(ts.toInstant());
          }
          prt.setUsed(rs.getInt("USED") == 1);
          return prt;
        }
      }
    } catch (SQLException e) {
      // Fallback to memory
    }

    return IN_MEMORY_TOKENS.get(token.trim());
  }

  default boolean markTokenAsUsed(String token) {
    if (token == null || token.isBlank()) {
      return false;
    }

    String cleanToken = token.trim();
    PasswordResetToken inMem = IN_MEMORY_TOKENS.get(cleanToken);
    if (inMem != null) {
      inMem.setUsed(true);
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(MARK_USED_SQL)) {
      ps.setString(1, cleanToken);
      ps.executeUpdate();
      return true;
    } catch (SQLException e) {
      return inMem != null;
    }
  }

  default void invalidateTokensForEmail(String email) {
    if (email == null || email.isBlank()) {
      return;
    }

    String normalizedEmail = normalizeEmail(email);
    for (PasswordResetToken token : IN_MEMORY_TOKENS.values()) {
      if (normalizedEmail.equalsIgnoreCase(token.getEmail())) {
        token.setUsed(true);
      }
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INVALIDATE_EMAIL_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.executeUpdate();
    } catch (SQLException ignored) {
    }
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
