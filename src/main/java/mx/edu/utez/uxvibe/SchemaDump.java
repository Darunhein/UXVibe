package mx.edu.utez.uxvibe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class SchemaDump {
    public static void main(String[] args) {
        try (Connection conn = ConexionBD.getInstancia().getConnection();
             Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT USER, SYSDATE FROM dual")) {
                if (rs.next()) {
                    System.out.println("CONEXION_OK user=" + rs.getString(1) + " fecha=" + rs.getTimestamp(2));
                }
            }

            new mx.edu.utez.uxvibe.dao.PasswordResetDao() {
            }.ensureTableExists();
            int upgraded = new mx.edu.utez.uxvibe.dao.UserDao() {
            }.upgradePlaintextPasswords();
            System.out.println("password_upgrades=" + upgraded);

            System.out.println("--- USER_TABLES ---");
            try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM user_tables ORDER BY 1")) {
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }

            System.out.println("--- LOGIN QUERY ---");
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT NOMBRE_COMPLETO, EMAIL, PASSWORD FROM USUARIOS WHERE 1=0")) {
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.println(rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
                }
            }
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) AS TOTAL, "
                            + "SUM(CASE WHEN PASSWORD LIKE 'pbkdf2$%' THEN 1 ELSE 0 END) AS HASHED "
                            + "FROM USUARIOS")) {
                if (rs.next()) {
                    System.out.println("usuarios=" + rs.getInt("TOTAL") + " hashed=" + rs.getInt("HASHED"));
                }
            }
            String[] tables = {
                    "USUARIOS", "PARTICIPANTES", "RESPUESTAS", "PRUEBAS", "PASSWORD_RESETS"
            };
            for (String table : tables) {
                dumpTable(stmt, table);
            }
        } catch (Exception e) {
            System.err.println("Error de conexion: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void dumpTable(Statement stmt, String table) {
        System.out.println("--- Table: " + table + " ---");
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE 1=0")) {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.println(rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i)
                        + " " + rsmd.getPrecision(i) + ")");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
