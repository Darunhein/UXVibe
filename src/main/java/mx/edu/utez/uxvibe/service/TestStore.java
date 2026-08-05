package mx.edu.utez.uxvibe.service;

import mx.edu.utez.uxvibe.model.TestItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestStore {
    private static final TestStore INSTANCE = new TestStore();
    private final Map<String, List<TestItem>> testsByUser = new LinkedHashMap<>();

    private TestStore() {
    }

    public static TestStore getInstance() {
        return INSTANCE;
    }

    public synchronized void createTest(String email, String name) {
        String normalizedEmail = normalize(email);
        if (normalizedEmail.isEmpty() || name == null || name.trim().isEmpty()) {
            return;
        }

        List<TestItem> tests = testsByUser.computeIfAbsent(normalizedEmail, key -> new ArrayList<>());
        tests.add(new TestItem(name.trim(), LocalDate.now()));
    }

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
