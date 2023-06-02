package org.example.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.database.InitDatabase.getInstance;

public class TransactionManager {

    public boolean makeTransaction(AccountModel fromAccount, AccountModel toAccount, TransactionModel transaction, UserModel currentUser) {
        String selectUserAccountQuery = "SELECT id FROM accounts  WHERE account_number = ? AND balance >= ? AND user_id = ? AND user_id IN (SELECT id FROM users WHERE online = 1)";
        String selectReceiverAccQuery = "SELECT id FROM accounts WHERE account_number = ?";
        String updateFromAccountQuery = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
        String updateToAccountQuery = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        String insertTransactionQuery = "INSERT INTO transactions (sender_acc_id, receiver_acc_id, transaction_value) VALUES (?, ?, ?)";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement selectFromAccountStatement = connection.prepareStatement(selectUserAccountQuery);
             PreparedStatement selectToAccountStatement = connection.prepareStatement(selectReceiverAccQuery);
             PreparedStatement updateFromAccountStatement = connection.prepareStatement(updateFromAccountQuery);
             PreparedStatement updateToAccountStatement = connection.prepareStatement(updateToAccountQuery);
             PreparedStatement insertTransactionStatement = connection.prepareStatement(insertTransactionQuery)) {

            connection.setAutoCommit(false);

            selectFromAccountStatement.setLong(1, fromAccount.getAccountNumber());
            selectFromAccountStatement.setDouble(2, transaction.getTransactionValue());
            selectFromAccountStatement.setInt(3, currentUser.getId());

            ResultSet fromResultSet = selectFromAccountStatement.executeQuery();

            if (fromResultSet.next()) {
                transaction.setSenderId(fromResultSet.getInt("id"));

                selectToAccountStatement.setLong(1, toAccount.getAccountNumber());

                ResultSet toResultSet = selectToAccountStatement.executeQuery();

                if (toResultSet.next()) {
                    transaction.setReceiverId(toResultSet.getInt("id"));

                    updateFromAccountStatement.setDouble(1, transaction.getTransactionValue());
                    updateFromAccountStatement.setLong(2, transaction.getSenderId());
                    updateFromAccountStatement.executeUpdate();

                    updateToAccountStatement.setDouble(1, transaction.getTransactionValue());
                    updateToAccountStatement.setLong(2, transaction.getReceiverId());
                    updateToAccountStatement.executeUpdate();

                    insertTransactionStatement.setLong(1, transaction.getSenderId());
                    insertTransactionStatement.setLong(2, transaction.getReceiverId());
                    insertTransactionStatement.setDouble(3, transaction.getTransactionValue());
                    insertTransactionStatement.executeUpdate();

                    connection.commit();

                    return true;
                }
            }
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
    public List<Map<String, Object>> getTransactionHistory(UserModel currentUser, AccountModel account, LocalDate fromDate, LocalDate toDate) {
        List<Map<String, Object>> transactions = new ArrayList<>();

        String selectTransactionsQuery = "SELECT transactions.*, sender.account_number AS sender_account_number, sender_user.name AS sender_name, receiver.account_number AS receiver_account_number, receiver_user.name AS receiver_name " +
                "FROM transactions " +
                "JOIN accounts AS sender ON transactions.sender_acc_id = sender.id " +
                "JOIN accounts AS receiver ON transactions.receiver_acc_id = receiver.id " +
                "JOIN users AS sender_user ON sender.user_id = sender_user.id " +
                "JOIN users AS receiver_user ON receiver.user_id = receiver_user.id " +
                "WHERE ((sender.account_number = ? AND sender.user_id = ?) OR (receiver.account_number = ? AND receiver.user_id = ?)) " +
                "AND transactions.time BETWEEN ? AND ? " +
                "ORDER BY transactions.time DESC";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement selectTransactionStatement = connection.prepareStatement(selectTransactionsQuery)) {

            selectTransactionStatement.setLong(1, account.getAccountNumber());
            selectTransactionStatement.setInt(2, currentUser.getId());
            selectTransactionStatement.setLong(3, account.getAccountNumber());
            selectTransactionStatement.setInt(4, currentUser.getId());
            selectTransactionStatement.setTimestamp(5, Timestamp.valueOf(fromDate.atTime(LocalTime.MIN)));
            selectTransactionStatement.setTimestamp(6, Timestamp.valueOf(toDate.atTime(LocalTime.MAX)));

            ResultSet resultSet = selectTransactionStatement.executeQuery();

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
