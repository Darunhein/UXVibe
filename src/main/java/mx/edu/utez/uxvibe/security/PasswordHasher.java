package mx.edu.utez.uxvibe.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
  static final String PREFIX = "pbkdf2$";
  private static final int ITERATIONS = 120000;
  private static final int KEY_LENGTH = 256;
  private static final int SALT_LENGTH = 16;
  private static final SecureRandom RANDOM = new SecureRandom();

  private PasswordHasher() {
  }

  public static boolean isHashed(String stored) {
    return stored != null && stored.startsWith(PREFIX);
  }

  public static String hash(String password) {
    if (password == null) {
      throw new IllegalArgumentException("password");
    }
    byte[] salt = new byte[SALT_LENGTH];
    RANDOM.nextBytes(salt);
    byte[] derived = pbkdf2(password.toCharArray(), salt, ITERATIONS);
    return PREFIX + ITERATIONS + "$"
        + Base64.getEncoder().encodeToString(salt) + "$"
        + Base64.getEncoder().encodeToString(derived);
  }

  public static boolean matches(String password, String stored) {
    if (password == null || stored == null) {
      return false;
    }
    if (!isHashed(stored)) {
      return stored.equals(password);
    }
    String[] parts = stored.split("\\$");
    if (parts.length != 4) {
      return false;
    }
    try {
      int iterations = Integer.parseInt(parts[1]);
      byte[] salt = Base64.getDecoder().decode(parts[2]);
      byte[] expected = Base64.getDecoder().decode(parts[3]);
      byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
      return MessageDigest.isEqual(expected, actual);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
    try {
      PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
      return factory.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
    }
  }
}
