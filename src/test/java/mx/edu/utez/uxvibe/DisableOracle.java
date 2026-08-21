package mx.edu.utez.uxvibe;

/**
 * Present only on the test classpath so ConexionBD can refuse live Oracle
 * during unit tests, even when local-secrets.properties exists.
 */
public final class DisableOracle {
  static {
    System.setProperty("uxvibe.disable.oracle", "true");
  }

  private DisableOracle() {
  }
}
