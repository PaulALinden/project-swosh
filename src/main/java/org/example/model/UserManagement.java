package org.example.model;

import org.example.database.InitDatabase;
import org.example.database.PasswordCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.database.InitDatabase.getInstance;

public class UserManagement extends UserModel {


    public LocalDateTime getWhenCreated(int userId) {
        String query = "SELECT created FROM users WHERE id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                String createdValue = result.getString("created");
                setCreated(LocalDateTime.parse(createdValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                return getCreated();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return null;
    }

    public void createUser(String name, long identityNumber, String password, long account, long balance) {
        setName(name);
        setIdentityNumber(identityNumber);
        setPassword(password);

        String userQuery = "INSERT INTO users (name, identity_number, password) VALUES (?, ?, ?)";
        String accountQuery = "INSERT INTO accounts (account_number, balance, user_id) VALUES (?, ?, ?)";

        try (Connection connection = getInstance().getConnection()) {
            // Start transaction
            connection.setAutoCommit(false);

            try (PreparedStatement userStatement = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {

                String userPassword = PasswordCrypt.Encrypt(getPassword());

                userStatement.setString(1, getName());
                userStatement.setLong(2, getIdentityNumber());
                userStatement.setString(3, userPassword);

                userStatement.executeUpdate();

                // Retrieve the generated keys (last inserted ID)
                ResultSet generatedKeys = userStatement.getGeneratedKeys();
                int userId = -1;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }

                AccountModel accountModel = new AccountModel();

                accountModel.setAccountNumber(account);
                accountModel.setBalance(balance);
                accountModel.setUserId(userId);

                accountStatement.setLong(1, accountModel.getAccountNumber());
                accountStatement.setDouble(2, accountModel.getBalance());
                accountStatement.setInt(3, accountModel.getUserId());

                accountStatement.executeUpdate();

                // Commit the transaction if both queries are successful
                connection.commit();
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println(e);
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    public UserModel verifyLogin(long identityNumber, String password) {

        setIdentityNumber(identityNumber);
        setPassword(password);

        String query = "SELECT id,name,identity_number,password FROM users WHERE identity_number=?";
        String loginQuery = "UPDATE users SET online = 1 WHERE id = ?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement identityStatement = connection.prepareStatement(query);
             PreparedStatement loginStatement = connection.prepareStatement(loginQuery)) {

            identityStatement.setLong(1, getIdentityNumber());

            ResultSet resultSet = identityStatement.executeQuery();

            if (resultSet.next()) {

                String fetchedPass = resultSet.getString("password");
                String name = resultSet.getString("name");
                long idNumber = resultSet.getLong("identity_number");
                int userId = resultSet.getInt("id");

                boolean isPass = PasswordCrypt.Verify(getPassword(), fetchedPass);

                System.out.println("~~~~~~~~~~~~~~");
                if (isPass) {

                    loginStatement.setInt(1, userId);
                    loginStatement.executeUpdate();

                    UserModel userModel = new UserModel();
                    userModel.setId(userId);
                    userModel.setName(name);
                    userModel.setIdentityNumber(idNumber);
                    userModel.setPassword(fetchedPass);

                    return userModel;
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

    public boolean updateUserName(String name, int id) {

        String sqlUp = "UPDATE users SET name = ? WHERE id = ?";
        try (Connection connection = InitDatabase.getInstance().getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUp)) {

                connection.setAutoCommit(false);

                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, id);
                int rowsAff = preparedStatement.executeUpdate();

                if (rowsAff == 1) {
                    connection.commit();
                    return true;
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

    public boolean updatePassword(String password, String newPassword, int id, String userPassword) {

        String sqlUp = "UPDATE users SET password = ? WHERE id = ? AND online = 1";

        boolean isUser = PasswordCrypt.Verify(password, userPassword);

        if (isUser) {
            String updatedPassword = PasswordCrypt.Encrypt(newPassword);

            try (Connection connection = InitDatabase.getInstance().getConnection()) {

                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUp)) {
                    connection.setAutoCommit(false);

                    preparedStatement.setString(1, updatedPassword);
                    preparedStatement.setInt(2, id);

                    preparedStatement.executeUpdate();

                    int rowsAff = preparedStatement.executeUpdate();

                    if (rowsAff == 1) {
                        return true;
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

    public boolean deleteUser(String password, String userPassword, int id){

        String query = "DELETE FROM users WHERE id = ? AND password = ? AND online = 1";
        boolean isUser = PasswordCrypt.Verify(password,userPassword);

        if (isUser) {

            try (Connection connection = InitDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    connection.setAutoCommit(false);

                    statement.setInt(1, id);
                    statement.setString(2, userPassword);

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

    public boolean updateIdentityNumber(long newIdentityNumber, int id){
        String sqlUp = "UPDATE users SET identity_number = ? WHERE id = ? AND online = 1";

        try (Connection connection = InitDatabase.getInstance().getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUp)) {
                connection.setAutoCommit(false);

                preparedStatement.setLong(1, newIdentityNumber);
                preparedStatement.setInt(2, id);

                preparedStatement.executeUpdate();

                int rowsAff = preparedStatement.executeUpdate();

                if (rowsAff == 1) {
                    return true;
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
}
