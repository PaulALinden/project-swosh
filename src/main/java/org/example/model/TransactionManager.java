package org.example.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.database.InitDatabase.getInstance;

public class TransactionManager extends TransactionModel{

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
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

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
                    setReceiverId(toResultSet.getInt("id"));

                    // Deduct the amount from the fromAccount
                    updateFromStatement.setDouble(1, getTransactionValue());
                    updateFromStatement.setLong(2, getSenderId());
                    updateFromStatement.executeUpdate();

                    // Add the amount to the toAccount
                    updateToStatement.setDouble(1, getTransactionValue());
                    updateToStatement.setLong(2, getReceiverId());
                    updateToStatement.executeUpdate();

                    // Insert a record in the transactions table
                    insertStatement.setLong(1, getSenderId());
                    insertStatement.setLong(2, getReceiverId());
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

    public List<Map<String, Object>> getTransactionHistory(UserModel user, long account, LocalDate startDateTime, LocalDate endDateTime) {
        List<Map<String, Object>> transactions = new ArrayList<>();

        String trHistQuery = "SELECT transactions.*, sender.account_number AS sender_account_number, sender_user.name AS sender_name, receiver.account_number AS receiver_account_number, receiver_user.name AS receiver_name " +
                "FROM transactions " +
                "JOIN accounts AS sender ON transactions.sender_acc_id = sender.id " +
                "JOIN accounts AS receiver ON transactions.receiver_acc_id = receiver.id " +
                "JOIN users AS sender_user ON sender.user_id = sender_user.id " +
                "JOIN users AS receiver_user ON receiver.user_id = receiver_user.id " +
                "WHERE ((sender.account_number = ? AND sender.user_id = ?) OR (receiver.account_number = ? AND receiver.user_id = ?)) " +
                "AND transactions.time BETWEEN ? AND ? " +
                "ORDER BY transactions.time DESC";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(trHistQuery)) {

            preparedStatement.setLong(1, account);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.setLong(3, account);
            preparedStatement.setInt(4, user.getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(endDateTime.atTime(LocalTime.MIN)));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime.atTime(LocalTime.MAX)));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> transactionMap = new HashMap<>();

                double amount = resultSet.getDouble("transaction_value");
                Timestamp time = resultSet.getTimestamp("time");
                String senderAccountNumber = resultSet.getString("sender_account_number");
                String senderName = resultSet.getString("sender_name");
                String receiverAccountNumber = resultSet.getString("receiver_account_number");
                String receiverName = resultSet.getString("receiver_name");

                transactionMap.put("amount", amount);
                transactionMap.put("time", time);
                transactionMap.put("senderAccountNumber", senderAccountNumber);
                transactionMap.put("senderName", senderName);
                transactionMap.put("receiverAccountNumber", receiverAccountNumber);
                transactionMap.put("receiverName", receiverName);

                transactions.add(transactionMap);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }
}
