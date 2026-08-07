package mx.edu.utez.uxvibe.dao;

import java.util.List;
import mx.edu.utez.uxvibe.model.TestItem;

public interface TestDao {
  void createTest(
    String email,
    String name,
    String description,
    String systemLink
  );

  List<TestItem> listByUser(String email);
}
