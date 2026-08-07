package mx.edu.utez.uxvibe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import mx.edu.utez.uxvibe.model.ParticipantItem;
import org.junit.jupiter.api.Test;

class ParticipantStoreTest {

  @Test
  void storesParticipantsPerUserAndTest() {
    ParticipantStore store = ParticipantStore.getInstance();
    String email = "participant-test-" + System.nanoTime() + "@uxvibe.test";
    String testName = "Prueba de usabilidad";

    ParticipantItem firstParticipant = store.registerCompletion(
      email,
      testName,
      LocalDateTime.now().minusMinutes(5)
    );
    ParticipantItem secondParticipant = store.registerCompletion(
      email,
      testName,
      LocalDateTime.now().minusMinutes(3)
    );
    List<ParticipantItem> participants = store.listByUserAndTest(
      email,
      testName
    );

    assertNotNull(firstParticipant);
    assertNotNull(secondParticipant);
    assertEquals(2, participants.size());
    assertEquals("Participante 1", participants.get(0).getName());
    assertEquals("Participante 2", participants.get(1).getName());
  }

  @Test
  void reusesTheActiveParticipantNameForTheFinalReport() {
    ParticipantStore store = ParticipantStore.getInstance();
    String email = "participant-report-" + System.nanoTime() + "@uxvibe.test";
    String testName = "Prueba de usabilidad";

    store.saveSurveyResponse(
      email,
      testName,
      "Participante provisional",
      "q1",
      "5"
    );

    ParticipantItem participant = store.registerCompletion(
      email,
      testName,
      LocalDateTime.now().minusMinutes(4)
    );

    assertNotNull(participant);
    assertEquals("Participante provisional", participant.getName());
  }
}
