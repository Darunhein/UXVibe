package mx.edu.utez.uxvibe;

import java.io.InputStream;
import java.util.Properties;

public final class AppSettings {
  private static final Properties LOCAL = loadLocal();

  private AppSettings() {
  }

  public static String get(String envKey, String propertyKey) {
    String value = System.getenv(envKey);
    if (notBlank(value)) {
      return value.trim();
    }
    value = System.getProperty(propertyKey);
    if (notBlank(value)) {
      return value.trim();
    }
    value = LOCAL.getProperty(propertyKey);
    if (notBlank(value)) {
      return value.trim();
    }
    return null;
  }

  public static String get(String envKey, String propertyKey, String defaultValue) {
    String value = get(envKey, propertyKey);
    return value == null ? defaultValue : value;
  }

  private static boolean notBlank(String value) {
    return value != null && !value.isBlank();
  }

  private static Properties loadLocal() {
    Properties properties = new Properties();
    try (InputStream in = AppSettings.class.getClassLoader()
        .getResourceAsStream("local-secrets.properties")) {
      if (in != null) {
        properties.load(in);
      }
    } catch (Exception ignored) {
    }
    return properties;
  }
}
