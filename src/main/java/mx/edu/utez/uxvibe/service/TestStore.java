package mx.edu.utez.uxvibe.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.edu.utez.uxvibe.dao.TestDao;
import mx.edu.utez.uxvibe.model.TestItem;

public class TestStore implements TestDao {

  private static final TestStore INSTANCE = new TestStore();
  private final Map<String, List<TestItem>> testsByUser = new LinkedHashMap<>();

  private TestStore() {}

  public static TestStore getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized void createTest(
    String email,
    String name,
    String description,
    String systemLink
  ) {
    String normalizedEmail = normalize(email);
    if (normalizedEmail.isEmpty() || name == null || name.trim().isEmpty()) {
      return;
    }

    List<TestItem> tests = testsByUser.computeIfAbsent(normalizedEmail, key ->
      new ArrayList<>()
    );
    tests.add(
      new TestItem(
        name.trim(),
        description,
        systemLink,
        LocalDate.now(Clock.system(ZoneId.of("America/Mexico_City")))
      )
    );
  }

  @Override
  public synchronized List<TestItem> listByUser(String email) {
    String normalizedEmail = normalize(email);
    List<TestItem> tests = testsByUser.get(normalizedEmail);
    if (tests == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(tests);
  }

  private String normalize(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }
}
