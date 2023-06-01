package org.example.model;

import org.example.database.InitDatabase;
import org.example.database.PasswordCrypt;

import java.sql.*;

import static org.example.database.InitDatabase.getInstance;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class UserManager {

    public void createUser(UserModel newUser, AccountModel firstAccount) {
        String userQuery = "INSERT INTO users (name, identity_number, password) VALUES (?, ?, ?)";
        String accountQuery = "INSERT INTO accounts (account_number, balance, user_id) " + "SELECT ?, ?, users.id FROM users WHERE users.id = ? " + "AND NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = ?)";

        try (Connection connection = getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement userStatement = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS); PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {

                String userPassword = PasswordCrypt.Encrypt(newUser.getPassword());

                userStatement.setString(1, newUser.getName());
                userStatement.setLong(2, newUser.getIdentityNumber());
                userStatement.setString(3, userPassword);

                userStatement.executeUpdate();

                ResultSet generatedKeys = userStatement.getGeneratedKeys();
                int userId = -1;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }

                firstAccount.setUserId(userId);

                accountStatement.setLong(1, firstAccount.getAccountNumber());
                accountStatement.setDouble(2, firstAccount.getBalance());
                accountStatement.setInt(3, firstAccount.getUserId());
                accountStatement.setLong(4, firstAccount.getAccountNumber());

                int rowsAffected = accountStatement.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                } else {
                    connection.rollback();
                    System.out.println("Account with the same account number already exists");
                }
            } catch (SQLException e) {
                connection.rollback();
                System.out.println(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public UserModel userLogin(long identityNumber, String password) {

        String query = "SELECT id,name,identity_number,password,online FROM users WHERE identity_number=?";
        String loginQuery = "UPDATE users SET online = 1 WHERE id = ?";

        try (Connection connection = getInstance().getConnection(); PreparedStatement identityStatement = connection.prepareStatement(query); PreparedStatement loginStatement = connection.prepareStatement(loginQuery)) {

            identityStatement.setLong(1, identityNumber);

            ResultSet resultSet = identityStatement.executeQuery();

            if (resultSet.next()) {

                String fetchedPass = resultSet.getString("password");
                String name = resultSet.getString("name");
                long idNumber = resultSet.getLong("identity_number");
                int userId = resultSet.getInt("id");
                boolean online = resultSet.getBoolean("online");
                boolean isPass = PasswordCrypt.Verify(password, fetchedPass);

                System.out.println("~~~~~~~~~~~~~~");
                if (isPass) {

                    loginStatement.setInt(1, userId);
                    loginStatement.executeUpdate();

                    UserModel user = new UserModel();

                    user.setId(userId);
                    user.setName(name);
                    user.setIdentityNumber(idNumber);
                    user.setPassword(fetchedPass);
                    user.setOnline(online);

                    return user;
                } else {
                    System.out.println("Wrong username or password");
                }
            }

            if (!resultSet.next()) {
                System.out.println("Wrong username or password");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
    public boolean updateUserName(UserModel user, String name, int id) {
        String sqlUp = "UPDATE users SET name = ? WHERE id = ?";
        String sqlSel = "SELECT name FROM users WHERE id = ?";

        try (Connection connection = InitDatabase.getInstance().getConnection()) {
            try (PreparedStatement updateStatement = connection.prepareStatement(sqlUp);
                 PreparedStatement selectStatement = connection.prepareStatement(sqlSel)) {

                connection.setAutoCommit(false);

                updateStatement.setString(1, name);
                updateStatement.setInt(2, id);
                int rowsAff = updateStatement.executeUpdate();

                if (rowsAff == 1) {
                    connection.commit();

                    selectStatement.setInt(1, id);
                    ResultSet resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        user.setName(resultSet.getString("name"));
                        return true;
                    }
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
        return false;
    }
    public boolean updatePassword(String password, String newPassword, UserModel user) {
        String sqlUp = "UPDATE users SET password = ? WHERE id = ? AND online = 1";
        boolean isUser = PasswordCrypt.Verify(password, user.getPassword());

        if (isUser) {
            String updatedPassword = PasswordCrypt.Encrypt(newPassword);

            try (Connection connection = InitDatabase.getInstance().getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUp)) {
                    connection.setAutoCommit(false);

                    preparedStatement.setString(1, updatedPassword);
                    preparedStatement.setInt(2, user.getId());

                    int rowsAff = preparedStatement.executeUpdate();

                    if (rowsAff == 1) {
                        connection.commit();

                        String sqlSel = "SELECT password FROM users WHERE id = ?";
                        try (PreparedStatement selectStatement = connection.prepareStatement(sqlSel)) {
                            selectStatement.setInt(1, user.getId());
                            ResultSet resultSet = selectStatement.executeQuery();

                            if (resultSet.next()) {
                                user.setPassword(resultSet.getString("password"));
                                return true;
                            }
                        }
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
    public boolean deleteUser(String password, UserModel user) {
        String userQuery = "DELETE FROM users WHERE id = ? AND password = ? AND online = 1";
        String accountsQuery = "DELETE FROM accounts WHERE user_id = ?";

        boolean isUser = PasswordCrypt.Verify(password, user.getPassword());

        if (isUser) {

            try (Connection connection = InitDatabase.getInstance().getConnection()) {
                try (PreparedStatement userStatement = connection.prepareStatement(userQuery); PreparedStatement accountsStatement = connection.prepareStatement(accountsQuery)) {
                    connection.setAutoCommit(false);

                    userStatement.setInt(1, user.getId());
                    userStatement.setString(2, user.getPassword());

                    accountsStatement.setInt(1, user.getId());

                    int userRowsAffected = userStatement.executeUpdate();
                    int accountsRowsAffected = accountsStatement.executeUpdate();

                    connection.commit();

                    return userRowsAffected > 0 && accountsRowsAffected > 0;
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
    public boolean updateIdentityNumber(long newIdentityNumber, UserModel user) {
        String sqlUp = "UPDATE users SET identity_number = ? WHERE id = ? AND online = 1";

        try (Connection connection = InitDatabase.getInstance().getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUp)) {
                connection.setAutoCommit(false);

                preparedStatement.setLong(1, newIdentityNumber);
                preparedStatement.setInt(2, user.getId());

                int rowsAff = preparedStatement.executeUpdate();

                if (rowsAff == 1) {
                    connection.commit();

                    // Retrieve the updated identity number from the database
                    String sqlSel = "SELECT identity_number FROM users WHERE id = ?";
                    try (PreparedStatement selectStatement = connection.prepareStatement(sqlSel)) {
                        selectStatement.setInt(1, user.getId());
                        ResultSet resultSet = selectStatement.executeQuery();

                        if (resultSet.next()) {
                            user.setIdentityNumber(resultSet.getLong("identity_number"));
                            return true;
                        }
                    }
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
        return false;
    }

    public void setUserOffline(UserModel user) {
        String query = "UPDATE users SET online = 0 WHERE id = ?";

        try (Connection connection = getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, user.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
