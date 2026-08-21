package mx.edu.utez.uxvibe.service;

import java.util.List;
import mx.edu.utez.uxvibe.dao.UserDao;
import mx.edu.utez.uxvibe.model.UserAccount;

public class UserStore implements UserDao {

  private static final UserStore INSTANCE = new UserStore();
  private final UserDao dao = new UserDao() {
  };

  private UserStore() {
  }

  public static UserStore getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized boolean register(UserAccount account) {
    return dao.register(account);
  }

  @Override
  public synchronized UserAccount authenticate(String email, String password) {
    return dao.authenticate(email, password);
  }

  @Override
  public synchronized boolean exists(String email) {
    return dao.exists(email);
  }

  @Override
  public synchronized List<UserAccount> list() {
    return dao.list();
  }

  @Override
  public synchronized boolean resetPassword(String email, String newPassword) {
    return dao.resetPassword(email, newPassword);
  }

  @Override
  public synchronized int upgradePlaintextPasswords() {
    return dao.upgradePlaintextPasswords();
  }
}
