package org.example.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.*;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class InitDatabase {

    private static InitDatabase instance;
    private MysqlDataSource dataSource;

    private InitDatabase() {
    }

    public static synchronized InitDatabase getInstance() {
        if (instance == null) {
            instance = new InitDatabase();
            instance.initializeDatabase();
        }
        return instance;
    }

    private void initializeDatabase() {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("src/main/java/org/example/config.json")) {
            Object obj = parser.parse(reader);
            JSONObject json = (JSONObject) obj;

            final String USERNAME = (String) json.get("USERNAME");
            final String PASSWORD = (String) json.get("PASSWORD");
            final String URL = (String) json.get("URL");
            final int PORT = Integer.parseInt(json.get("PORT").toString());
            final String DATABASE = (String) json.get("DATABASE");

            System.out.print("Configuring data source...");
            dataSource = new MysqlDataSource();
            dataSource.setUser(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setUrl("jdbc:mysql://" + URL + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC");
            dataSource.setUseSSL(false);
            System.out.print("done!\n");
        } catch (SQLException e) {
            System.out.print("failed!\n");
            System.out.println(e);
            System.exit(0);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.print("failed!\n");
            System.out.println(e);
            System.exit(0);
            return null;
        }
    }

    public static void initTables() {
        String[] tableQuery = {
        "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), identity_number BIGINT, password VARCHAR(250), created DATETIME DEFAULT CURRENT_TIMESTAMP, online BOOLEAN)",
        "CREATE TABLE IF NOT EXISTS accounts (id INT PRIMARY KEY AUTO_INCREMENT, account_number BIGINT, balance DECIMAL(10,2), user_id INT, created DATETIME DEFAULT CURRENT_TIMESTAMP)",
        "CREATE TABLE IF NOT EXISTS transactions (id INT PRIMARY KEY AUTO_INCREMENT, sender_acc_id INT, receiver_acc_id INT, transaction_value DECIMAL(10,2), time DATETIME DEFAULT CURRENT_TIMESTAMP)"
        };
        for (String query: tableQuery) {

            try (Connection connection = InitDatabase.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e);
            }
        }
        System.out.println("Tables set..");
    }
}

