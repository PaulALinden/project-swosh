package org.example.model;

import org.example.database.PasswordCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.database.InitDatabase.getInstance;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class UserModel {

    private String name;
    private long identityNumber;
    private String password;

    private LocalDateTime created;
    public UserModel(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(long identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
                accountStatement.setLong(2, accountModel.getBalance());
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


    public boolean verifyLogin(long identityNumber, String password) {

        setIdentityNumber(identityNumber);
        setPassword(password);

        String query = "SELECT name,identity_number,password FROM users WHERE identity_number=?";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, getIdentityNumber());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){

                String fetchedPass = resultSet.getString("password");
                long id = resultSet.getLong("identity_number");
                String name = resultSet.getString("name");

                boolean isPass = PasswordCrypt.Verify(getPassword(), fetchedPass);

                System.out.println("~~~~~~~~~~~~~~");
                if (isPass){
                    System.out.println("Access granted");
                    System.out.println("Welcome " + name);
                    System.out.println(id);
                    return true;
                }
                else{
                    System.out.println("Access denied");
                    System.out.println("Wrong username or password");
                    return false;
                }
            }

            if (!resultSet.next()){
                System.out.println("Wrong username or password");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
}
