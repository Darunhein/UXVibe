package mx.edu.utez.uxvibe.dao;

import java.util.List;
import mx.edu.utez.uxvibe.model.UserAccount;

public interface UserDao {
  boolean register(UserAccount account);

  UserAccount authenticate(String email, String password);

  boolean exists(String email);

  List<UserAccount> list();
}
