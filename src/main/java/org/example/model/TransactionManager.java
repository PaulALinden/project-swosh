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
        String selectUserAccountQuery = "SELECT account_number FROM accounts  WHERE account_number = ? AND balance >= ? AND user_id = ? AND user_id IN (SELECT id FROM users WHERE online = 1)";
        String selectReceiverAccQuery = "SELECT account_number FROM accounts WHERE account_number = ?";
        String updateFromAccountQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String updateToAccountQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionQuery = "INSERT INTO transactions (sender_acc, receiver_acc, transaction_value) VALUES (?, ?, ?)";

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
                transaction.setSenderAccountNumber(fromResultSet.getLong("account_number"));

                selectToAccountStatement.setLong(1, toAccount.getAccountNumber());

                ResultSet toResultSet = selectToAccountStatement.executeQuery();

                if (toResultSet.next()) {
                    transaction.setReceiverAccountNumber(toResultSet.getLong("account_number"));

                    updateFromAccountStatement.setDouble(1, transaction.getTransactionValue());
                    updateFromAccountStatement.setLong(2, transaction.getSenderAccountNumber());
                    updateFromAccountStatement.executeUpdate();

                    updateToAccountStatement.setDouble(1, transaction.getTransactionValue());
                    updateToAccountStatement.setLong(2, transaction.getReceiverAccountNumber());
                    updateToAccountStatement.executeUpdate();

                    insertTransactionStatement.setLong(1, transaction.getSenderAccountNumber());
                    insertTransactionStatement.setLong(2, transaction.getReceiverAccountNumber());
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
    public List<Map<String, Object>> getTransactionHistory(UserModel user, AccountModel account, LocalDate fromDate, LocalDate toDate) {
        List<Map<String, Object>> transactions = new ArrayList<>();

        String selectTransactionsQuery = "SELECT * FROM transactions WHERE (sender_acc = ? OR receiver_acc = ?) AND time BETWEEN ? AND ? AND EXISTS (SELECT 1 FROM users WHERE id = ? AND online = true)";

        try (Connection connection = getInstance().getConnection();
             PreparedStatement selectTransactionStatement = connection.prepareStatement(selectTransactionsQuery)) {

            selectTransactionStatement.setLong(1, account.getAccountNumber());
            selectTransactionStatement.setLong(2, account.getAccountNumber());
            selectTransactionStatement.setTimestamp(3, Timestamp.valueOf(fromDate.atTime(LocalTime.MIN)));
            selectTransactionStatement.setTimestamp(4, Timestamp.valueOf(toDate.atTime(LocalTime.MAX)));
            selectTransactionStatement.setLong(5, user.getId());

            ResultSet resultSet = selectTransactionStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> transactionMap = new HashMap<>();

                double amount = resultSet.getDouble("transaction_value");
                Timestamp time = resultSet.getTimestamp("time");
                String senderAccountNumber = resultSet.getString("sender_acc");
                String receiverAccountNumber = resultSet.getString("receiver_acc");

                transactionMap.put("amount", amount);
                transactionMap.put("time", time);
                transactionMap.put("senderAccountNumber", senderAccountNumber);
                transactionMap.put("receiverAccountNumber", receiverAccountNumber);

                transactions.add(transactionMap);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }
}
