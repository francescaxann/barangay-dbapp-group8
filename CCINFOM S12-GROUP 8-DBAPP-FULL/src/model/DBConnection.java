package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/barangay_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Put your MySQL password inside "".

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ⬅️ Explicitly load the MySQL driver
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
