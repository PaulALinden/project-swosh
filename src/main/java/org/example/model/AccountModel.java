package org.example.model;

import org.example.database.InitDatabase;
import org.example.database.PasswordCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.database.InitDatabase.getInstance;

public class AccountModel {

    private int id;
    private long accountNumber;
    private double balance;
    private int userId;

    private LocalDateTime created;
    public AccountModel() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean createAccount(long account, long balance, int userId){

        setAccountNumber(account);
        setBalance(balance);

        String query = "INSERT INTO accounts (account_number, balance, user_id) " +
                "SELECT ?, ?, ? FROM dual " +
                "WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = ?) " +
                "AND EXISTS (SELECT 1 FROM users WHERE id = ?)";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setDouble(2, balance);
            preparedStatement.setInt(3, userId);
            preparedStatement.setLong(4, accountNumber);
            preparedStatement.setInt(5, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    public boolean deleteAccount(int id, String userPassword, long accountNumber, String password){

        String query = "DELETE FROM accounts WHERE user_id = ? AND account_number = ?";

        boolean isUser = PasswordCrypt.Verify(password,userPassword);

        if (isUser) {
            try (Connection connection = InitDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    connection.setAutoCommit(false);

                    statement.setInt(1, id);
                    statement.setLong(2, accountNumber);

                    statement.executeUpdate();

                    return true;
                } catch (SQLException e) {
                    connection.rollback();
                    System.out.println(e);
                } finally {
                    connection.setAutoCommit(true);
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }

    public List<Map<String, Object>> getAccountsFromUser(int userId) {
        List<Map<String, Object>> accounts = new ArrayList<>();

        String query = "SELECT users.name, users.identity_number, users.created, " +
                "accounts.account_number, accounts.balance " +
                "FROM users " +
                "JOIN accounts ON users.id = accounts.user_id " +
                "WHERE users.id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean isFirstRow = true;

            while (resultSet.next()) {
                Map<String, Object> accountsMap = new HashMap<>();

                if (isFirstRow) {
                    String name = resultSet.getString("name");
                    long identityNumber = resultSet.getLong("identity_number");
                    Timestamp created = resultSet.getTimestamp("created");

                    accountsMap.put("name", name);
                    accountsMap.put("identityNumber", identityNumber);
                    accountsMap.put("created", created);

                    isFirstRow = false;
                }

                long accountNumber = resultSet.getLong("account_number");
                double balance = resultSet.getDouble("balance");

                accountsMap.put("accountNumber", accountNumber);
                accountsMap.put("balance", balance);

                accounts.add(accountsMap);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }



}
