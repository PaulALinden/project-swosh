package org.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.example.database.InitDatabase.getInstance;

public class TransactionModel {

    private int senderId;
    private int reciverId;

    private double transactionValue;

    private LocalDateTime time;

    public TransactionModel(){}

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReciverId() {
        return reciverId;
    }

    public void setReciverId(int reciverId) {
        this.reciverId = reciverId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(double transactionValue) {
        this.transactionValue = transactionValue;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean makeTransfer(long fromAccount, long toAccount, double amount, int userId) {
        String selectUserAccQuery = "SELECT id FROM accounts  WHERE account_number = ? AND balance >= ? AND user_id = ? AND user_id IN (SELECT id FROM users WHERE online = 1)";
        String selectReceiverAccQuery = "SELECT id FROM accounts WHERE account_number = ?";
        String updateFromAccountQuery = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String updateToAccountQuery = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        String insertQuery = "INSERT INTO transactions (sender_acc_id, receiver_acc_id, transaction_value) VALUES (?, ?, ?)";

        setTransactionValue(amount);

        try (Connection connection = getInstance().getConnection();
             PreparedStatement fromStatement = connection.prepareStatement(selectUserAccQuery);
             PreparedStatement toStatement = connection.prepareStatement(selectReceiverAccQuery);
             PreparedStatement updateFromStatement = connection.prepareStatement(updateFromAccountQuery);
             PreparedStatement updateToStatement = connection.prepareStatement(updateToAccountQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)){

            // Begin the transaction
            connection.setAutoCommit(false);

            // Check if the fromAccount has sufficient balance
            fromStatement.setLong(1, fromAccount);
            fromStatement.setDouble(2, getTransactionValue());
            fromStatement.setInt(3, userId);
            ResultSet resultSet = fromStatement.executeQuery();

            if (resultSet.next()) {
                setSenderId(resultSet.getInt("id"));

                // Check if the toAccount exists
                toStatement.setLong(1, toAccount);
                ResultSet toResultSet = toStatement.executeQuery();

                if (toResultSet.next()) {
                    setReciverId(toResultSet.getInt("id"));

                    // Deduct the amount from the fromAccount
                    updateFromStatement.setDouble(1, getTransactionValue());
                    updateFromStatement.setLong(2, getSenderId());
                    updateFromStatement.executeUpdate();

                    // Add the amount to the toAccount
                    updateToStatement.setDouble(1, getTransactionValue());
                    updateToStatement.setLong(2, getReciverId());
                    updateToStatement.executeUpdate();

                    // Insert a record in the transactions table
                    insertStatement.setLong(1, getSenderId());
                    insertStatement.setLong(2, getReciverId());
                    insertStatement.setDouble(3, getTransactionValue());
                    insertStatement.executeUpdate();

                    // Commit the transaction
                    connection.commit();

                    return true;
                }
            }
            // Rollback the transaction if the conditions are not met
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public void getTransactionHistory(){
        //Konto id, hämta....
    }

}
