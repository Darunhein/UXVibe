package mx.edu.utez.uxvibe.util;

import java.util.UUID;

public final class ParticipantIds {
  private ParticipantIds() {
  }

  public static String newFallbackName() {
    return "Participante " + UUID.randomUUID().toString().substring(0, 8);
  }
}
