package mx.edu.utez.uxvibe.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QuestionNumbersTest {

  @Test
  void mapsKnownSurveyFieldsToStableNumbers() {
    assertEquals(QuestionNumbers.AUDIO_MIC, QuestionNumbers.toNumber("audio_mic"));
    assertEquals(QuestionNumbers.AUDIO, QuestionNumbers.toNumber("audio"));
    assertEquals(QuestionNumbers.AGE, QuestionNumbers.toNumber("age"));
    assertEquals(QuestionNumbers.SATISFACTION, QuestionNumbers.toNumber("satisfaction"));
    assertEquals(11, QuestionNumbers.toNumber("q1"));
    assertEquals(25, QuestionNumbers.toNumber("q15"));
  }

  @Test
  void roundTripsKnownNumbersBackToFieldNames() {
    assertEquals("audio", QuestionNumbers.toName(QuestionNumbers.AUDIO));
    assertEquals("audio_mic", QuestionNumbers.toName(QuestionNumbers.AUDIO_MIC));
    assertTrue(QuestionNumbers.isAudio(QuestionNumbers.AUDIO_MIC));
    assertEquals("gender", QuestionNumbers.toName(QuestionNumbers.GENDER));
    assertEquals("q5", QuestionNumbers.toName(15));
    assertEquals("q15", QuestionNumbers.toName(25));
  }

  @Test
  void treatsAudioAliasesAsAudioRows() {
    assertTrue(QuestionNumbers.isAudio(QuestionNumbers.toNumber("audio_url")));
    assertTrue(QuestionNumbers.isAudioName("AUDIO"));
  }
}
