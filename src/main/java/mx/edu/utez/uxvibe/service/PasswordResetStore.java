package mx.edu.utez.uxvibe.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import mx.edu.utez.uxvibe.dao.PasswordResetDao;
import mx.edu.utez.uxvibe.model.PasswordResetToken;

public class PasswordResetStore implements PasswordResetDao {

  private static final PasswordResetStore INSTANCE = new PasswordResetStore();
  private static final Duration TOKEN_EXPIRATION = Duration.ofMinutes(30);
  private final PasswordResetDao dao = new PasswordResetDao() {
  };

  private PasswordResetStore() {
    try {
      dao.ensureTableExists();
    } catch (Throwable ignored) {
    }
  }

  public static PasswordResetStore getInstance() {
    return INSTANCE;
  }

  public synchronized String createToken(String email) {
    if (email == null || email.isBlank()) {
      return null;
    }

    String cleanEmail = email.trim().toLowerCase();

    // Invalidate any existing unused tokens for this email
    dao.invalidateTokensForEmail(cleanEmail);

    // Generate a secure random token (UUID without hyphens + additional randomness)
    String token = UUID.randomUUID().toString().replace("-", "") +
        UUID.randomUUID().toString().substring(0, 8);

    Instant expiresAt = Instant.now().plus(TOKEN_EXPIRATION);
    PasswordResetToken resetToken = new PasswordResetToken(token, cleanEmail, expiresAt);

    boolean saved = dao.saveToken(resetToken);
    return saved ? token : null;
  }

  public synchronized PasswordResetToken validateToken(String token) {
    if (token == null || token.isBlank()) {
      return null;
    }

    PasswordResetToken resetToken = dao.findByToken(token.trim());
    if (resetToken == null) {
      return null;
    }

    if (resetToken.isValid()) {
      return resetToken;
    }

    return null;
  }

  public synchronized boolean resetPasswordWithToken(String token, String newPassword) {
    PasswordResetToken resetToken = validateToken(token);
    if (resetToken == null || newPassword == null || newPassword.isBlank()) {
      return false;
    }

    boolean updated = UserStore.getInstance().resetPassword(resetToken.getEmail(), newPassword);
    if (updated) {
      dao.markTokenAsUsed(resetToken.getToken());
      return true;
    }

    return false;
  }
}
