package mx.edu.utez.uxvibe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SchemaDump {
    public static void main(String[] args) {
        try (Connection conn = ConexionBD.getInstancia().getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("--- Table: PARTICIPANTES ---");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM PARTICIPANTES WHERE 1=0")) {
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.println(rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println("--- Table: RESPUESTAS ---");
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM RESPUESTAS WHERE 1=0")) {
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    System.out.println(rsmd.getColumnName(i) + " (" + rsmd.getColumnTypeName(i) + ")");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
