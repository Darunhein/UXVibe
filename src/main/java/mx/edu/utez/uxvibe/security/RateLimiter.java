package mx.edu.utez.uxvibe.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RateLimiter {

  private static final int MAX_ATTEMPTS = 5;
  private static final long COOLDOWN_MILLIS = 2 * 60 * 1000; // 2 minutes

  private static final Map<String, AttemptInfo> ATTEMPTS = new ConcurrentHashMap<>();

  private RateLimiter() {
  }

  public static boolean isAllowed(String key) {
    if (key == null || key.isBlank()) {
      return true;
    }

    String cleanKey = key.trim().toLowerCase();
    long now = System.currentTimeMillis();

    AttemptInfo info = ATTEMPTS.get(cleanKey);
    if (info == null) {
      return true;
    }

    if (now - info.firstAttemptTime > COOLDOWN_MILLIS) {
      ATTEMPTS.remove(cleanKey);
      return true;
    }

    return info.count < MAX_ATTEMPTS;
  }

  public static void recordFailure(String key) {
    if (key == null || key.isBlank()) {
      return;
    }

    String cleanKey = key.trim().toLowerCase();
    long now = System.currentTimeMillis();

    ATTEMPTS.compute(cleanKey, (k, current) -> {
      if (current == null || (now - current.firstAttemptTime > COOLDOWN_MILLIS)) {
        return new AttemptInfo(1, now);
      }
      return new AttemptInfo(current.count + 1, current.firstAttemptTime);
    });
  }

  public static void reset(String key) {
    if (key != null) {
      ATTEMPTS.remove(key.trim().toLowerCase());
    }
  }

  private static class AttemptInfo {
    final int count;
    final long firstAttemptTime;

    AttemptInfo(int count, long firstAttemptTime) {
      this.count = count;
      this.firstAttemptTime = firstAttemptTime;
    }
  }
}
