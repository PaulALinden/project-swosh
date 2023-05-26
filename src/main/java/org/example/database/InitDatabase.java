package org.example.database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class InitDatabase {

    private static InitDatabase instance;
    private MysqlDataSource dataSource;
    private String url = "localhost";
    private int port = 3306;
    private String database = "swosh";
    private String username = "root";
    private String password = "";

    private InitDatabase() {
        // private constructor
    }

    public static synchronized InitDatabase getInstance() {
        if (instance == null) {
            instance = new InitDatabase();
            instance.initializeDatabase();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            System.out.print("Configuring data source...");
            dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setUrl("jdbc:mysql://" + url + ":" + port + "/" + database + "?serverTimezone=UTC");
            dataSource.setUseSSL(false);
            System.out.print("done!\n");
        } catch (SQLException e) {
            System.out.print("failed!\n");
            System.out.println(e);
            System.exit(0);
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
        String[] tableQuerys = {
                /* user */       "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), identity_number BIGINT, password VARCHAR(250), created DATETIME DEFAULT CURRENT_TIMESTAMP)",
                /* account */    "CREATE TABLE IF NOT EXISTS accounts (id INT PRIMARY KEY AUTO_INCREMENT, account_number BIGINT, balance DECIMAL(10,2), user_id INT, created DATETIME DEFAULT CURRENT_TIMESTAMP)",
                /* transaction */"CREATE TABLE IF NOT EXISTS transactions (id INT PRIMARY KEY AUTO_INCREMENT, sender_id INT, receiver_id INT, transaction_value DECIMAL(10,2), time DATETIME DEFAULT CURRENT_TIMESTAMP)"
        };
        for (String query: tableQuerys) {

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

