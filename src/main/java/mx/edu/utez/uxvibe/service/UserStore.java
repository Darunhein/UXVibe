package mx.edu.utez.uxvibe.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.edu.utez.uxvibe.dao.UserDao;
import mx.edu.utez.uxvibe.model.UserAccount;

public class UserStore implements UserDao {

  private static final UserStore INSTANCE = new UserStore();
  private final Map<String, UserAccount> accounts = new LinkedHashMap<>();

  private UserStore() {}

  public static UserStore getInstance() {
    return INSTANCE;
  }

  @Override
  public synchronized boolean register(UserAccount account) {
    if (
      account == null ||
      account.getEmail() == null ||
      account.getPassword() == null
    ) {
      return false;
    }

    String normalizedEmail = normalizeEmail(account.getEmail());
    if (normalizedEmail.isEmpty() || accounts.containsKey(normalizedEmail)) {
      return false;
    }

    accounts.put(normalizedEmail, account);
    return true;
  }

  @Override
  public synchronized UserAccount authenticate(String email, String password) {
    if (email == null || password == null) {
      return null;
    }

    UserAccount account = accounts.get(normalizeEmail(email));
    if (account == null) {
      return null;
    }

    return password.equals(account.getPassword()) ? account : null;
  }

  @Override
  public synchronized boolean exists(String email) {
    return email != null && accounts.containsKey(normalizeEmail(email));
  }

  @Override
  public synchronized List<UserAccount> list() {
    return new ArrayList<>(accounts.values());
  }

  private String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase();
  }
}
