package mx.edu.utez.uxvibe.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {

  @Test
  void hashesAndVerifiesPassword() {
    String hash = PasswordHasher.hash("correctHorseBattery");
    assertTrue(PasswordHasher.isHashed(hash));
    assertTrue(PasswordHasher.matches("correctHorseBattery", hash));
    assertFalse(PasswordHasher.matches("wrong-password", hash));
  }

  @Test
  void acceptsLegacyPlaintextOnce() {
    assertTrue(PasswordHasher.matches("plain-secret", "plain-secret"));
    assertFalse(PasswordHasher.isHashed("plain-secret"));
  }
}
