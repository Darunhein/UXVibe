package mx.edu.utez.uxvibe;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;

public class ConexionBD {
    private static final String USUARIO = "ADMIN";
    private static final String PASSWORD = "Alfaro180107";
    private static final String WALLET_PASSWORD = "Alfaro180107";
    private static final String NOMBRE_WALLET = "Wallet_Integradora";
    private static final String JDBC_URL = "jdbc:oracle:thin:@"
            + "(description= (retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.us-phoenix-1.oraclecloud.com))(connect_data=(service_name=gc5a7c8f4fbdb11_integradora_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))";

    private static final String TRUSTSTORE = "truststore.jks";
    private static final String KEYSTORE = "keystore.jks";

    private static ConexionBD instancia;

    private ConexionBD() {
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null)
            instancia = new ConexionBD();
        return instancia;
    }

    public Connection getConnection() throws SQLException {
        return crearConexion();
    }

    private static Connection crearConexion() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró ojdbc11. Rebuild del WAR y redeploy en Tomcat.", e);
        }
        if (Boolean.getBoolean("uxvibe.disable.oracle") || testsAreOnClasspath()) {
            throw new SQLException("Faltan credenciales Oracle. (disabled for tests)");
        }
        String rutaWallet = resolverRutaWallet();
        System.setProperty("javax.net.ssl.trustStore", rutaWallet + "/" + TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", WALLET_PASSWORD);
        System.setProperty("javax.net.ssl.keyStore", rutaWallet + "/" + KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", WALLET_PASSWORD);
        return DriverManager.getConnection(JDBC_URL, USUARIO, PASSWORD);
    }

    public static boolean isUnavailable(SQLException e) {
        if (e == null) {
            return false;
        }
        String state = e.getSQLState();
        if (state != null && state.startsWith("08")) {
            return true;
        }
        int code = e.getErrorCode();
        if (code == 17002 || code == 17008 || code == 18730) {
            return true;
        }
        String message = e.getMessage();
        if (message == null) {
            return e.getCause() instanceof SQLException && isUnavailable((SQLException) e.getCause());
        }
        String lower = message.toLowerCase();
        return lower.contains("faltan credenciales")
                || lower.contains("no se encontró la carpeta")
                || lower.contains("network adapter")
                || lower.contains("i/o error")
                || lower.contains("io error")
                || lower.contains("could not establish");
    }

    private static boolean testsAreOnClasspath() {
        try {
            Class.forName("mx.edu.utez.uxvibe.DisableOracle");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static String resolverRutaWallet() throws SQLException {
        String envPath = System.getenv("ORACLE_WALLET_PATH");
        if (envPath != null && !envPath.isBlank())
            return normalizarRuta(envPath);
        String propPath = System.getProperty("oracle.wallet.path");
        if (propPath != null && !propPath.isBlank())
            return normalizarRuta(propPath);
        String rutaClasspath = buscarEnClasspath();
        if (rutaClasspath != null)
            return rutaClasspath;
        String rutaLocal = System.getProperty("user.dir") + File.separator + NOMBRE_WALLET;
        if (existeWallet(rutaLocal))
            return normalizarRuta(rutaLocal);
        throw new SQLException("No se encontró la carpeta " + NOMBRE_WALLET + ".");
    }

    private static String buscarEnClasspath() {
        URL trustStore = ConexionBD.class.getClassLoader()
                .getResource(NOMBRE_WALLET + "/" + TRUSTSTORE);
        if (trustStore == null || !"file".equals(trustStore.getProtocol()))
            return null;
        try {
            return normalizarRuta(Paths.get(trustStore.toURI()).getParent().toString());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static boolean existeWallet(String ruta) {
        return new File(ruta, TRUSTSTORE).exists();
    }

    private static String normalizarRuta(String ruta) {
        return ruta.replace("\\", "/");
    }

    public static void main(String[] args) {
        try (Connection conn = ConexionBD.getInstancia().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM dual")) {
            if (rs.next())
                System.out.println("Conexion OK. Fecha del servidor: " + rs.getTimestamp(1));
        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
