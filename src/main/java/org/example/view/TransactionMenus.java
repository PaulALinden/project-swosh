package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.model.UserModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TransactionMenus extends SubMenu{

    public static void showTransactionMenu(UserModel user, Scanner scanner, AccountController accountController, TransactionController transactionController) {
        System.out.println("1. Make transaction");
        System.out.println("2. Transaction history account");

        String input = scanner.nextLine();

        switch (input) {
            case "1" -> showCreateTransaction(user, accountController, transactionController);
            case "2" -> showTransactionHistory(user, accountController, transactionController);
            default -> System.out.println("Invalid input");
        }
    }

    public static void showCreateTransaction(UserModel user, AccountController accountController, TransactionController transactionController) {

        List<Map<String, Object>> accounts = accountController.getUsersAccounts(user);

        System.out.println("Accounts available:");
        for (Map<String, Object> account : accounts) {

            Long accountNumber = (Long) account.get("accountNumber");
            Double balance = (Double) account.get("balance");

            if (accountNumber != null && balance != null) {
                System.out.println("Account: " + accountNumber + " | Balance: " + balance);
            }
        }

        System.out.println("Transfer from (account):");
        String fromAccount = SCANNER.nextLine();

        System.out.println("Write amount:");
        String amount = SCANNER.nextLine();

        System.out.println("Transfer to (account):");
        String toAccount = SCANNER.nextLine();

        if (fromAccount.isEmpty() || amount.isEmpty() || toAccount.isEmpty()) {
            System.out.println("Input can't be empty. Please try again.");
            return;
        }

        String transfer = transactionController.makeTransfer(fromAccount, toAccount, amount, user);

        System.out.println(transfer);
    }

    public static void showTransactionHistory(UserModel user, AccountController accountController, TransactionController transactionController) {
        List<Map<String, Object>> accounts = accountController.getUsersAccounts(user);

        System.out.println("Accounts available:");
        for (Map<String, Object> account : accounts) {

            Long accountNumber = (Long) account.get("accountNumber");
            Double balance = (Double) account.get("balance");

            if (accountNumber != null && balance != null) {
                System.out.println("Account: " + accountNumber + " | Balance: " + balance);
            }
        }

        System.out.println("Check history of account (write account):");
        String account = SCANNER.nextLine();

        System.out.println("From:");
        String from = SCANNER.nextLine();

        System.out.println("To date:");
        String to = SCANNER.nextLine();

        if (account.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println("Input can't be empty. Please try again.");
            return;
        }

        List<Map<String, Object>> history = transactionController.getTransactions(user, account, from, to);

        if (history == null){
            System.out.println("Something went wrong. Check your input.");
            return;
        }

        for (Map<String, Object> transaction : history) {
            double amount = (double) transaction.get("amount");
            String senderName = (String) transaction.get("senderName");
            String receiverName = (String) transaction.get("receiverName");
            long receiverAccountNumber = Long.parseLong(transaction.get("receiverAccountNumber").toString());
            long senderAccountNumber = Long.parseLong(transaction.get("senderAccountNumber").toString());
            Timestamp time = (Timestamp) transaction.get("time");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedTime = dateFormat.format(time);

            System.out.println("From: " + senderName + ", account: " + senderAccountNumber);
            System.out.println("To: " + receiverName + ", account: " + receiverAccountNumber);
            System.out.println("Amount: " + amount + ", time: " + formattedTime);
            System.out.println("-----------------------------------------------------------");
        }


    }
}
