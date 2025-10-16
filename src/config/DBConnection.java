package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(
            	"jdbc:mysql://switchback.proxy.rlwy.net:19363/restaurant_db?allowPublicKeyRetrieval=true&useSSL=false",
                "root", "nOvpYbPYvORjvXMXXVfsRrtCmINFjzLy"); // change user/pass
        }
        return conn;
    }
}
