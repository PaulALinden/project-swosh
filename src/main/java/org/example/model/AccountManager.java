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

    public boolean createAccount(AccountModel newAccount, UserModel currentUser) {

        String insertAccountQuery = "INSERT INTO accounts (account_number, balance, user_id) " +
                "SELECT ?, ?, ? FROM dual " +
                "WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = ?) " +
                "AND EXISTS (SELECT 1 FROM users WHERE id = ?)";
        try (Connection connection = getInstance().getConnection();) {

            try (PreparedStatement insertAccountStatement = connection.prepareStatement(insertAccountQuery)) {
                connection.setAutoCommit(false);

                insertAccountStatement.setLong(1, newAccount.getAccountNumber());
                insertAccountStatement.setDouble(2, newAccount.getBalance());
                insertAccountStatement.setInt(3, currentUser.getId());
                insertAccountStatement.setLong(4, newAccount.getAccountNumber());
                insertAccountStatement.setInt(5, currentUser.getId());

                int rowsAffected = insertAccountStatement.executeUpdate();

                if (rowsAffected == 1) {

                    return true;
                }

            } catch (SQLException e) {
                System.out.println(e);
            }
            finally {
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e){
            System.out.println(e);
        }
        return false;
    }
    public boolean deleteAccount(UserModel currentUser, AccountModel accountToDelete, String password) {

        String deleteAccountQuery = "DELETE FROM accounts WHERE user_id = ? AND account_number = ?";
        String accountCheckQuery = "SELECT COUNT(*) AS accountCount FROM accounts WHERE user_id = ?";

        boolean isUser = PasswordCrypt.Verify(password, currentUser.getPassword());

        if (isUser) {

            try (Connection connection = InitDatabase.getInstance().getConnection()) {

                try (PreparedStatement accountCheckStatement = connection.prepareStatement(accountCheckQuery);
                     PreparedStatement deleteAccountStatement = connection.prepareStatement(deleteAccountQuery)) {
                    connection.setAutoCommit(false);

                    accountCheckStatement.setInt(1, currentUser.getId());
                    ResultSet resultSet = accountCheckStatement.executeQuery();

                    resultSet.next();
                    int accountCount = resultSet.getInt("accountCount");

                    if (accountCount > 1) {

                        deleteAccountStatement.setInt(1, accountToDelete.getUserId());
                        deleteAccountStatement.setLong(2, accountToDelete.getAccountNumber());

                        deleteAccountStatement.executeUpdate();

                        connection.commit();
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                catch (SQLException e) {
                    connection.rollback();
                    System.out.println(e);
                }
                finally {
                    connection.setAutoCommit(true);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return false;
    }
    public List<Map<String, Object>> getAccountsByUserId(AccountModel account) {

        List<Map<String, Object>> accountList = new ArrayList<>();

        String selectAccountsQuery = "SELECT users.name, users.identity_number, users.created, " +
                "accounts.account_number, accounts.balance " +
                "FROM users " +
                "JOIN accounts ON users.id = accounts.user_id " +
                "WHERE users.id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement selectAccountsStatement = connection.prepareStatement(selectAccountsQuery)) {

            selectAccountsStatement.setInt(1, account.getUserId());

            ResultSet resultSet = selectAccountsStatement.executeQuery();

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

                accountList.add(accountMap);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accountList;
    }
}
