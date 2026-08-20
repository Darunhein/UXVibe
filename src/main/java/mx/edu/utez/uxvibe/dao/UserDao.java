package mx.edu.utez.uxvibe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mx.edu.utez.uxvibe.ConexionBD;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.model.UserRole;
import mx.edu.utez.uxvibe.security.PasswordHasher;

public interface UserDao {
  String INSERT_SQL = "INSERT INTO USUARIOS (NOMBRE_COMPLETO, EMAIL, PASSWORD, ROL) VALUES (?, ?, ?, ?)";
  String FIND_BY_EMAIL_SQL = "SELECT NOMBRE_COMPLETO, EMAIL, PASSWORD, ROL FROM USUARIOS WHERE LOWER(EMAIL)=LOWER(?)";
  String EXISTS_SQL = "SELECT 1 FROM USUARIOS WHERE LOWER(EMAIL)=LOWER(?)";
  String LIST_SQL = "SELECT NOMBRE_COMPLETO, EMAIL, PASSWORD, ROL FROM USUARIOS ORDER BY ID_USUARIO";
  String RESET_PASSWORD_SQL = "UPDATE USUARIOS SET PASSWORD = ? WHERE LOWER(EMAIL) = LOWER(?)";
  Map<String, UserAccount> IN_MEMORY_ACCOUNTS = initDefaultAccounts();

  static Map<String, UserAccount> initDefaultAccounts() {
    return new LinkedHashMap<>();
  }

  default boolean register(UserAccount account) {
    if (account == null) {
      return false;
    }
    String email = normalizeEmail(account.getEmail());
    if (email.isEmpty() || account.getPassword() == null) {
      return false;
    }

    if (IN_MEMORY_ACCOUNTS.containsKey(email)) {
      return false;
    }

    String hashedPassword = PasswordHasher.hash(account.getPassword());

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
      ps.setString(1, account.getFullName());
      ps.setString(2, email);
      ps.setString(3, hashedPassword);
      ps.setString(4, account.getRole());
      if (ps.executeUpdate() > 0) {
        IN_MEMORY_ACCOUNTS.put(email, cloneAccount(account, email, hashedPassword));
        return true;
      }
      return false;
    } catch (SQLException e) {
      if (e.getErrorCode() == 1) {
        return false;
      }
      if (ConexionBD.isUnavailable(e)) {
        IN_MEMORY_ACCOUNTS.put(email, cloneAccount(account, email, hashedPassword));
        return true;
      }
      e.printStackTrace();
      return false;
    }
  }

  default UserAccount authenticate(String email, String password) {
    if (email == null || password == null) {
      return null;
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_SQL)) {
      ps.setString(1, normalizeEmail(email));
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          UserAccount account = mapUser(rs);
          if (!PasswordHasher.matches(password, account.getPassword())) {
            return null;
          }
          if (!PasswordHasher.isHashed(account.getPassword())) {
            upgradeStoredPassword(normalizeEmail(email), password);
          }
          return publicCopy(account);
        }
      }
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        e.printStackTrace();
      }
    }

    UserAccount account = IN_MEMORY_ACCOUNTS.get(normalizeEmail(email));
    if (account == null || !PasswordHasher.matches(password, account.getPassword())) {
      return null;
    }
    return publicCopy(account);
  }

  default boolean exists(String email) {
    if (email == null) {
      return false;
    }

    String normalizedEmail = normalizeEmail(email);
    if (IN_MEMORY_ACCOUNTS.containsKey(normalizedEmail)) {
      return true;
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
      ps.setString(1, normalizedEmail);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  default List<UserAccount> list() {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(LIST_SQL);
        ResultSet rs = ps.executeQuery()) {
      List<UserAccount> accounts = new ArrayList<>();
      while (rs.next()) {
        accounts.add(mapUser(rs));
      }
      return accounts;
    } catch (SQLException e) {
      e.printStackTrace();
      return new ArrayList<>(IN_MEMORY_ACCOUNTS.values());
    }
  }

  default boolean resetPassword(String email, String newPassword) {
    if (email == null || newPassword == null) {
      return false;
    }
    String hashedPassword = PasswordHasher.hash(newPassword);
    String normalizedEmail = normalizeEmail(email);
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(RESET_PASSWORD_SQL)) {
      ps.setString(1, hashedPassword);
      ps.setString(2, normalizedEmail);
      int updated = ps.executeUpdate();
      if (updated > 0) {
        UserAccount account = IN_MEMORY_ACCOUNTS.get(normalizedEmail);
        if (account != null) {
          account.setPassword(hashedPassword);
        }
        return true;
      }
    } catch (SQLException e) {
      if (!ConexionBD.isUnavailable(e)) {
        return false;
      }
    }

    UserAccount account = IN_MEMORY_ACCOUNTS.get(normalizedEmail);
    if (account != null) {
      account.setPassword(hashedPassword);
      return true;
    }
    return false;
  }

  private static void upgradeStoredPassword(String email, String rawPassword) {
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(RESET_PASSWORD_SQL)) {
      String hashed = PasswordHasher.hash(rawPassword);
      ps.setString(1, hashed);
      ps.setString(2, email);
      ps.executeUpdate();
      UserAccount cached = IN_MEMORY_ACCOUNTS.get(email);
      if (cached != null) {
        cached.setPassword(hashed);
      }
    } catch (SQLException ignored) {
    }
  }

  private static UserAccount mapUser(ResultSet rs) throws SQLException {
    UserAccount account = new UserAccount();
    account.setFullName(rs.getString("NOMBRE_COMPLETO"));
    account.setEmail(rs.getString("EMAIL"));
    account.setPassword(rs.getString("PASSWORD"));
    account.setRole(rs.getString("ROL"));
    if (account.getRole() == null || account.getRole().isEmpty()) {
      account.setRole(UserRole.EVALUATOR);
    }
    return account;
  }

  private static UserAccount cloneAccount(UserAccount source, String email, String password) {
    UserAccount account = new UserAccount();
    account.setFullName(source.getFullName());
    account.setEmail(email);
    account.setPassword(password);
    account.setRole(source.getRole());
    return account;
  }

  private static UserAccount publicCopy(UserAccount source) {
    UserAccount account = new UserAccount();
    account.setFullName(source.getFullName());
    account.setEmail(source.getEmail());
    account.setPassword(null);
    account.setRole(source.getRole());
    return account;
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }
}
