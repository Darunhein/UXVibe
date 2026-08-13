package mx.edu.utez.uxvibe.service;

import java.util.List;
import mx.edu.utez.uxvibe.dao.TestDao;
import mx.edu.utez.uxvibe.model.TestItem;

public class TestStore implements TestDao {

  private static final TestStore INSTANCE = new TestStore();
  private final TestDao dao = new TestDao() {};

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
    dao.createTest(email, name, description, systemLink);
  }

  @Override
  public synchronized List<TestItem> listByUser(String email) {
    return dao.listByUser(email);
  }

  public synchronized boolean deleteTest(String email, String testName) {
    boolean ok = dao.deleteTest(email, testName);
    // ensure in-memory cache in this service is also cleaned if present
    try {
      java.lang.reflect.Field field = dao.getClass().getDeclaredField("IN_MEMORY_TESTS_BY_USER");
      field.setAccessible(true);
      Object mapObj = field.get(null);
      if (mapObj instanceof java.util.Map) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, java.util.List<TestItem>> map = (java.util.Map<String, java.util.List<TestItem>>) mapObj;
        map.remove(email == null ? null : email.trim().toLowerCase());
      }
    } catch (Throwable ignore) {
      // ignore reflection failures; DAO already clears its cache
    }
    return ok;
  }
}
