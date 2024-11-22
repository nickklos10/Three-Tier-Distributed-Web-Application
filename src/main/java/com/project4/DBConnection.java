package com.project4;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import jakarta.servlet.ServletContext;

/**
 * Utility class to manage database connections.
 */
public class DBConnection {

    /**
     * Establishes a database connection using the specified properties file.
     *
     * @param context         The ServletContext to access resources.
     * @param propertiesPath  The path to the properties file within the web application.
     * @return                A Connection object to the database.
     * @throws SQLException   If a database access error occurs.
     */
    public static Connection getConnection(ServletContext context, String propertiesPath) throws SQLException {
        Properties props = new Properties();
        try (InputStream input = context.getResourceAsStream(propertiesPath)) {
            if (input == null) {
                System.err.println("Properties file not found: " + propertiesPath);
                throw new SQLException("Unable to find properties file: " + propertiesPath);
            }
            props.load(input);
        } catch (IOException e) {
            throw new SQLException("Error loading DB properties: " + e.getMessage());
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");

        if (url == null || user == null || password == null || driver == null) {
            throw new SQLException("Database connection details missing in properties file.");
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + driver);
        }

        return DriverManager.getConnection(url, user, password);
    }
}