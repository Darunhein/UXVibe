package mx.edu.utez.uxvibe.service;

import static org.junit.jupiter.api.Assertions.*;

import mx.edu.utez.uxvibe.model.PasswordResetToken;
import mx.edu.utez.uxvibe.model.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordResetStoreTest {

  private PasswordResetStore resetStore;
  private UserStore userStore;

  @BeforeEach
  void setUp() {
    resetStore = PasswordResetStore.getInstance();
    userStore = UserStore.getInstance();
  }

  @Test
  void createsAndValidatesTokenSuccessfully() {
    String email = "reset-test-" + System.nanoTime() + "@uxvibe.test";
    String token = resetStore.createToken(email);

    assertNotNull(token);
    assertFalse(token.isBlank());

    PasswordResetToken found = resetStore.validateToken(token);
    assertNotNull(found);
    assertEquals(email.toLowerCase(), found.getEmail());
    assertFalse(found.isUsed());
    assertFalse(found.isExpired());
  }

  @Test
  void returnsNullForInvalidOrEmptyToken() {
    assertNull(resetStore.validateToken(null));
    assertNull(resetStore.validateToken(""));
    assertNull(resetStore.validateToken("non-existent-token-xyz-123"));
  }

  @Test
  void resetsPasswordAndConsumesToken() {
    String email = "user-reset-" + System.nanoTime() + "@uxvibe.test";
    UserAccount account = new UserAccount();
    account.setFullName("Test User");
    account.setEmail(email);
    account.setPassword("initialPass123");
    userStore.register(account);

    String token = resetStore.createToken(email);
    assertNotNull(token);

    boolean resetSuccess = resetStore.resetPasswordWithToken(token, "newSecurePassword456");
    assertTrue(resetSuccess);

    // Token should now be consumed and invalid
    assertNull(resetStore.validateToken(token));

    // Authenticating with old password should fail
    assertNull(userStore.authenticate(email, "initialPass123"));

    // Authenticating with new password should succeed
    UserAccount updated = userStore.authenticate(email, "newSecurePassword456");
    assertNotNull(updated);
    assertEquals(email.toLowerCase(), updated.getEmail().toLowerCase());
  }

  @Test
  void invalidatesPreviousTokensWhenNewOneIsGenerated() {
    String email = "multi-token-" + System.nanoTime() + "@uxvibe.test";
    String token1 = resetStore.createToken(email);
    String token2 = resetStore.createToken(email);

    assertNotNull(token1);
    assertNotNull(token2);
    assertNotEquals(token1, token2);

    // First token should be invalidated/marked used
    assertNull(resetStore.validateToken(token1));

    // Second token should be valid
    assertNotNull(resetStore.validateToken(token2));
  }
}
