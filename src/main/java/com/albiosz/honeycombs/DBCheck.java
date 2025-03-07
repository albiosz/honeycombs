package com.albiosz.honeycombs;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCheck {

    void waitUntilDBConnectionIsAvailable() {
        try {
            while (!checkDBConnection()) {
                System.out.println("Waiting for DB connection...");
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }

    boolean checkDBConnection() {
        Dotenv dotenv = Dotenv
                .configure()
                .ignoreIfMissing()
                .load();

        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String dbHost = dotenv.get("DB_HOST");
        String dbName = dotenv.get("DB_NAME");
        String dbPort = dotenv.get("DB_PORT");
        String dbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
