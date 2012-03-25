package models;

import java.sql.*;
import java.util.Properties;
import play.Play;

/**
 * Utility class for aditionnal databse management
 *
 * @author gehef
 */
public class DBUtils {

    public static Connection getConnection()
            throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", Play.configuration.getProperty("manialive.db.user"));
        connectionProps.put("password", Play.configuration.getProperty("manialive.db.pass"));

        conn = DriverManager.getConnection(
                Play.configuration.getProperty("manialive.db.url"),
                connectionProps);
        return conn;
    }
    
    
}
