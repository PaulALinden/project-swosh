package org.example.model;

import org.example.database.InitDatabase;
import org.example.database.PasswordCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.database.InitDatabase.getInstance;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class AccountManager {

    public boolean createAccount(AccountModel newAccount, UserModel user) {
        String query = "INSERT INTO accounts (account_number, balance, user_id) " +
                "SELECT ?, ?, ? FROM dual " +
                "WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = ?) " +
                "AND EXISTS (SELECT 1 FROM users WHERE id = ?)";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, newAccount.getAccountNumber());
            preparedStatement.setDouble(2, newAccount.getBalance());
            preparedStatement.setInt(3, user.getId());
            preparedStatement.setLong(4, newAccount.getAccountNumber());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    public boolean deleteAccount(UserModel user, AccountModel removeAccount, String password) {
        String query = "DELETE FROM accounts WHERE user_id = ? AND account_number = ?";
        String accountCheckQuery = "SELECT COUNT(*) AS accountCount FROM accounts WHERE user_id = ?";

        boolean isUser = PasswordCrypt.Verify(password, user.getPassword());

        if (isUser) {
            try (Connection connection = InitDatabase.getInstance().getConnection()) {
                try (PreparedStatement accountCheckStatement = connection.prepareStatement(accountCheckQuery);
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    connection.setAutoCommit(false);

                    accountCheckStatement.setInt(1, user.getId());
                    ResultSet accountCheckResult = accountCheckStatement.executeQuery();
                    accountCheckResult.next();
                    int accountCount = accountCheckResult.getInt("accountCount");

                    if (accountCount > 1) {
                        statement.setInt(1, removeAccount.getUserId());
                        statement.setLong(2, removeAccount.getAccountNumber());

                        statement.executeUpdate();

                        connection.commit();
                        return true;
                    } else {
                        System.out.println("User must have at least one account");
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    System.out.println(e);
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }


    public List<Map<String, Object>> getAccountsFromUser(AccountModel userId) {
        List<Map<String, Object>> accounts = new ArrayList<>();

        String query = "SELECT users.name, users.identity_number, users.created, " +
                "accounts.account_number, accounts.balance " +
                "FROM users " +
                "JOIN accounts ON users.id = accounts.user_id " +
                "WHERE users.id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId.getUserId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> accountMap = new HashMap<>();
                String name = resultSet.getString("name");
                long identityNumber = resultSet.getLong("identity_number");
                Timestamp created = resultSet.getTimestamp("created");
                long accountNumber = resultSet.getLong("account_number");
                double balance = resultSet.getDouble("balance");

                accountMap.put("name", name);
                accountMap.put("identityNumber", identityNumber);
                accountMap.put("created", created);
                accountMap.put("accountNumber", accountNumber);
                accountMap.put("balance", balance);

                accounts.add(accountMap);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }


}
