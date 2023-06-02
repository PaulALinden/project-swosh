package org.example.model;

import org.example.database.InitDatabase;
import org.example.database.PasswordCrypt;

import java.sql.*;

import static org.example.database.InitDatabase.getInstance;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class UserManager {

    public void createUser(UserModel user, AccountModel initialAccount) {
        
        String insertUserQuery = "INSERT INTO users (name, identity_number, password) VALUES (?, ?, ?)";
        
        String insertAccountQuery = "INSERT INTO accounts (account_number, balance, user_id) " + 
                "SELECT ?, ?, users.id FROM users WHERE users.id = ? " + 
                "AND NOT EXISTS (SELECT 1 FROM accounts WHERE account_number = ?)";

        try (Connection connection = getInstance().getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement userInsertStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement accountInsertStatement = connection.prepareStatement(insertAccountQuery)) {

                String encryptedPassword = PasswordCrypt.Encrypt(user.getPassword());

                userInsertStatement.setString(1, user.getName());
                userInsertStatement.setLong(2, user.getIdentityNumber());
                userInsertStatement.setString(3, encryptedPassword);

                userInsertStatement.executeUpdate();

                ResultSet generatedKeys = userInsertStatement.getGeneratedKeys();
                int userId = -1;

                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }

                initialAccount.setUserId(userId);

                accountInsertStatement.setLong(1, initialAccount.getAccountNumber());
                accountInsertStatement.setDouble(2, initialAccount.getBalance());
                accountInsertStatement.setInt(3, initialAccount.getUserId());
                accountInsertStatement.setLong(4, initialAccount.getAccountNumber());

                int rowsAffected = accountInsertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    connection.commit();
                }
                else {
                    connection.rollback();
                    System.out.println("Something went wrong. Check your input and try again.");
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

        String selectUserQuery = "SELECT id,name,identity_number,password,online FROM users WHERE identity_number=?";
        String setUserOnlineQuery = "UPDATE users SET online = 1 WHERE id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement userSelectStatement  = connection.prepareStatement(selectUserQuery);
             PreparedStatement userOnlineStatusStatement = connection.prepareStatement(setUserOnlineQuery)) {

            userSelectStatement .setLong(1, identityNumber);

            ResultSet resultSet = userSelectStatement .executeQuery();

            if (resultSet.next()) {

                String fetchedPass = resultSet.getString("password");
                String name = resultSet.getString("name");
                long idNumber = resultSet.getLong("identity_number");
                int userId = resultSet.getInt("id");
                boolean online = resultSet.getBoolean("online");
                boolean isPasswordCorrect = PasswordCrypt.Verify(password, fetchedPass);

                if (isPasswordCorrect) {

                    userOnlineStatusStatement.setInt(1, userId);
                    userOnlineStatusStatement.executeUpdate();

                    UserModel user = new UserModel();

                    user.setId(userId);
                    user.setName(name);
                    user.setIdentityNumber(idNumber);
                    user.setPassword(fetchedPass);
                    user.setOnline(online);

                    return user;
                }
                else {
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
    public boolean updateUserName(UserModel currentUser, String newName) {

        String updateUserNameQuery = "UPDATE users SET name = ? WHERE id = ?";
        String selectUserNameQuery = "SELECT name FROM users WHERE id = ?";

        try (Connection connection = InitDatabase.getInstance().getConnection()) {
            try (PreparedStatement userNameUpdateStatement = connection.prepareStatement(updateUserNameQuery);
                 PreparedStatement userNameSelectStatement = connection.prepareStatement(selectUserNameQuery)) {

                connection.setAutoCommit(false);

                userNameUpdateStatement.setString(1, newName);
                userNameUpdateStatement.setInt(2, currentUser.getId());

                int rowsAffected = userNameUpdateStatement.executeUpdate();

                if (rowsAffected == 1) {
                    connection.commit();

                    userNameSelectStatement.setInt(1, currentUser.getId());

                    ResultSet resultSet = userNameSelectStatement.executeQuery();

                    if (resultSet.next()) {

                        currentUser.setName(resultSet.getString("name"));
                        return true;
                    }
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
        return false;
    }

    public boolean updatePassword(String password, String newPassword, UserModel currentUser) {

        String passwordUpdateQuery = "UPDATE users SET password = ? WHERE id = ? AND online = 1";

        boolean isUser = PasswordCrypt.Verify(password, currentUser.getPassword());

        if (isUser) {

            String encryptedPassword = PasswordCrypt.Encrypt(newPassword);

            try (Connection connection = InitDatabase.getInstance().getConnection()) {

                try (PreparedStatement passwordUpdateStatement = connection.prepareStatement(passwordUpdateQuery)) {
                    connection.setAutoCommit(false);

                    passwordUpdateStatement.setString(1, encryptedPassword);
                    passwordUpdateStatement.setInt(2, currentUser.getId());

                    int rowsAffected = passwordUpdateStatement.executeUpdate();

                    if (rowsAffected == 1) {
                        connection.commit();

                        String selectPasswordQuery = "SELECT password FROM users WHERE id = ?";

                        try (PreparedStatement passwordSelectStatement = connection.prepareStatement(selectPasswordQuery)) {

                            passwordSelectStatement.setInt(1, currentUser.getId());

                            ResultSet resultSet = passwordSelectStatement.executeQuery();

                            if (resultSet.next()) {
                                currentUser.setPassword(resultSet.getString("password"));
                                return true;
                            }
                        }
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
    public boolean deleteUser(String password, UserModel currentUser) {

        String deleteUserQuery = "DELETE FROM users WHERE id = ? AND password = ? AND online = 1";
        String deleteUserAccountsQuery = "DELETE FROM accounts WHERE user_id = ?";

        boolean isUser = PasswordCrypt.Verify(password, currentUser.getPassword());

        if (isUser) {

            try (Connection connection = InitDatabase.getInstance().getConnection()) {

                try (PreparedStatement userDeleteStatement = connection.prepareStatement(deleteUserQuery);
                     PreparedStatement accountsDeleteStatement = connection.prepareStatement(deleteUserAccountsQuery)) {
                    connection.setAutoCommit(false);

                    userDeleteStatement.setInt(1, currentUser.getId());
                    userDeleteStatement.setString(2, currentUser.getPassword());

                    accountsDeleteStatement.setInt(1, currentUser.getId());

                    int userRowsAffected = userDeleteStatement.executeUpdate();
                    int accountsRowsAffected = accountsDeleteStatement.executeUpdate();

                    connection.commit();

                    return userRowsAffected > 0 && accountsRowsAffected > 0;
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
    public boolean updateIdentityNumber(long newIdentityNumber, UserModel currentUser) {

        String updateIdentityNumberQuery = "UPDATE users SET identity_number = ? WHERE id = ? AND online = 1";

        try (Connection connection = InitDatabase.getInstance().getConnection()) {

            try (PreparedStatement identityNumberUpdateStatement = connection.prepareStatement(updateIdentityNumberQuery)) {
                connection.setAutoCommit(false);

                identityNumberUpdateStatement.setLong(1, newIdentityNumber);
                identityNumberUpdateStatement.setInt(2, currentUser.getId());

                int rowsAffected = identityNumberUpdateStatement.executeUpdate();

                if (rowsAffected == 1) {
                    connection.commit();

                    String sqlSel = "SELECT identity_number FROM users WHERE id = ?";
                    
                    try (PreparedStatement identityNumberSelectStatement = connection.prepareStatement(sqlSel)) {
                        
                        identityNumberSelectStatement.setInt(1, currentUser.getId());
                        
                        ResultSet resultSet = identityNumberSelectStatement.executeQuery();

                        if (resultSet.next()) {
                            currentUser.setIdentityNumber(resultSet.getLong("identity_number"));
                            return true;
                        }
                    }
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
        return false;
    }

    public boolean setUserOffline(UserModel currentUser) {

        String updateUserStatusQuery = "UPDATE users SET online = 0 WHERE id = ?";

        try (Connection connection = getInstance().getConnection()) {

            try (PreparedStatement updateUserStatusStatement = connection.prepareStatement(updateUserStatusQuery)) {
                connection.setAutoCommit(false);

                updateUserStatusStatement.setInt(1, currentUser.getId());

                int rowsAffected = updateUserStatusStatement.executeUpdate();

                if (rowsAffected == 1) {
                    connection.commit();
                    return true;
                }
                else {
                    connection.rollback();
                    return false;
                }
            }
            catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
            finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
