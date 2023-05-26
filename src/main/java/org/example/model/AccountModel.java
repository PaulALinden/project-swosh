package org.example.model;

import org.example.database.PasswordCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.example.database.InitDatabase.getInstance;

public class AccountModel {

    private long accountNumber;
    private long balance;
    private int userId;

    private LocalDateTime created;

    public AccountModel() {

    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
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

    public void createAccount(long account, long balance, int id){

        setAccountNumber(account);
        setBalance(balance);
        setUserId(id);

        String query = "INSERT INTO accounts (account_number, balance, user_id) VALUES (?, ?, ?)";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {


            preparedStatement.setLong(1, getAccountNumber());
            preparedStatement.setLong(2, getBalance());
            preparedStatement.setInt(3, getUserId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}
