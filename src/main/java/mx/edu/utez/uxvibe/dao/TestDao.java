package mx.edu.utez.uxvibe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mx.edu.utez.uxvibe.ConexionBD;
import mx.edu.utez.uxvibe.model.TestItem;

public interface TestDao {
  String INSERT_SQL = "INSERT INTO PRUEBAS (EMAIL_USUARIO, NOMBRE, DESCRIPCION, SYSTEM_LINK, CREATED_ON) VALUES (?, ?, ?, ?, ?)";
  String LIST_BY_USER_SQL = "SELECT NOMBRE, DESCRIPCION, SYSTEM_LINK, CREATED_ON FROM PRUEBAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) ORDER BY CREATED_ON, ID_PRUEBA";
  String FIND_ID_SQL = "SELECT ID_PRUEBA FROM PRUEBAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE)=LOWER(?)";
  Map<String, List<TestItem>> IN_MEMORY_TESTS_BY_USER = new LinkedHashMap<>();
  String DELETE_TEST_SQL = "DELETE FROM PRUEBAS WHERE LOWER(EMAIL_USUARIO)=LOWER(?) AND LOWER(NOMBRE)=LOWER(?)";

  default void createTest(
      String email,
      String name,
      String description,
      String systemLink) {
    String normalizedEmail = normalizeEmail(email);
    if (normalizedEmail.isEmpty() || name == null || name.trim().isEmpty()) {
      return;
    }

    TestItem test = new TestItem(
        name.trim(),
        description,
        systemLink,
        LocalDate.now(ZoneId.of("America/Mexico_City")));
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, test.getName());
      ps.setString(3, test.getDescription());
      ps.setString(4, test.getSystemLink());
      ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Mexico_City"))));
      ps.executeUpdate();
      IN_MEMORY_TESTS_BY_USER.computeIfAbsent(normalizedEmail, key -> new ArrayList<>()).add(test);
    } catch (SQLException e) {
      if (ConexionBD.isUnavailable(e)) {
        IN_MEMORY_TESTS_BY_USER.computeIfAbsent(normalizedEmail, key -> new ArrayList<>()).add(test);
        return;
      }
      e.printStackTrace();
    }
  }

  default List<TestItem> listByUser(String email) {
    String normalizedEmail = normalizeEmail(email);
    if (normalizedEmail.isEmpty()) {
      return new ArrayList<>();
    }

    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(LIST_BY_USER_SQL)) {
      ps.setString(1, normalizedEmail);
      List<TestItem> tests = new ArrayList<>();
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          TestItem test = new TestItem();
          test.setName(rs.getString("NOMBRE"));
          test.setDescription(rs.getString("DESCRIPCION"));
          test.setSystemLink(rs.getString("SYSTEM_LINK"));
          Timestamp createdOn = rs.getTimestamp("CREATED_ON");
          if (createdOn != null) {
            test.setCreatedOn(createdOn.toLocalDateTime().toLocalDate());
          }
          tests.add(test);
        }
      }
      IN_MEMORY_TESTS_BY_USER.put(normalizedEmail, new ArrayList<>(tests));
      return tests;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<TestItem> cachedTests = IN_MEMORY_TESTS_BY_USER.get(normalizedEmail);
    if (cachedTests == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(cachedTests);
  }

  default long findIdByEmailAndName(String email, String testName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedName = testName == null ? "" : testName.trim();
    if (!normalizedEmail.isEmpty() && !normalizedName.isEmpty()) {
      try (
          Connection conn = ConexionBD.getInstancia().getConnection();
          PreparedStatement ps = conn.prepareStatement(FIND_ID_SQL)) {
        ps.setString(1, normalizedEmail);
        ps.setString(2, normalizedName);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            long id = rs.getLong("ID_PRUEBA");
            if (!rs.wasNull() && id > 0) {
              return id;
            }
          }
        }
      } catch (SQLException e) {
        if (!ConexionBD.isUnavailable(e)) {
          e.printStackTrace();
        }
      }
    }
    return -1;
  }

  private static String normalizeEmail(String email) {
    return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
  }

  default boolean deleteTest(String email, String testName) {
    String normalizedEmail = normalizeEmail(email);
    String normalizedTestName = testName == null ? "" : testName.trim();
    if (normalizedEmail.isEmpty() || normalizedTestName.isEmpty()) {
      return false;
    }
    boolean deletedOnDb = false;
    int affected = 0;
    try (
        Connection conn = ConexionBD.getInstancia().getConnection();
        PreparedStatement ps = conn.prepareStatement(DELETE_TEST_SQL)) {
      ps.setString(1, normalizedEmail);
      ps.setString(2, normalizedTestName);
      affected = ps.executeUpdate();
      deletedOnDb = true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    boolean removedFromMemory = false;
    List<TestItem> list = IN_MEMORY_TESTS_BY_USER.get(normalizedEmail);
    if (list != null) {
      removedFromMemory = list.removeIf(t -> t.getName() != null && t.getName().equalsIgnoreCase(normalizedTestName));
    }
    return (deletedOnDb && affected > 0) || (!deletedOnDb && removedFromMemory);
  }
}
