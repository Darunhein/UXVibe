package mx.edu.utez.uxvibe.dao;

import java.time.LocalDateTime;
import java.util.List;
import mx.edu.utez.uxvibe.model.ParticipantItem;

public interface ParticipantDao {
  default ParticipantItem registerCompletion(
    String email,
    String testName,
    LocalDateTime startedAt
  ) {
    return registerCompletion(email, testName, startedAt, null);
  }

  ParticipantItem registerCompletion(
    String email,
    String testName,
    LocalDateTime startedAt,
    String participantName
  );

  List<ParticipantItem> listByUserAndTest(String email, String testName);
}
