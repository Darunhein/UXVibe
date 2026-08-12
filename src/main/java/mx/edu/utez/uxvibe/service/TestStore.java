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
}
