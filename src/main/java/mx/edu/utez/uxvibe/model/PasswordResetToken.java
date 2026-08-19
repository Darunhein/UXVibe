package mx.edu.utez.uxvibe.model;

import java.io.Serializable;
import java.time.Instant;

public class PasswordResetToken implements Serializable {

  private String token;
  private String email;
  private Instant createdAt;
  private Instant expiresAt;
  private boolean used;

  public PasswordResetToken() {
    this.createdAt = Instant.now();
    this.used = false;
  }

  public PasswordResetToken(String token, String email, Instant expiresAt) {
    this.token = token;
    this.email = email;
    this.createdAt = Instant.now();
    this.expiresAt = expiresAt;
    this.used = false;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }

  public boolean isUsed() {
    return used;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }

  public boolean isExpired() {
    return expiresAt != null && Instant.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return !used && !isExpired();
  }
}
