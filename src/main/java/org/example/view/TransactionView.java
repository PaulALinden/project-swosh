package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.model.UserModel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.example.view.AccountView.showAccounts;

public class TransactionView{

    public static void showTransactionMenu(UserModel currentUser, Scanner scanner, AccountController accountController, TransactionController transactionController) {
        System.out.println("1. Make transaction.");
        System.out.println("2. Transaction history.");

        String userChoice = scanner.nextLine();

        switch (userChoice) {
            case "1" -> showMakeTransaction(currentUser, scanner, accountController, transactionController);
            case "2" -> showAccountTransactionHistory(currentUser, scanner, accountController, transactionController);
            default -> System.out.println("Invalid input. try again.");
        }
    }

    public static void showMakeTransaction(UserModel currentUser, Scanner scanner, AccountController accountController, TransactionController transactionController) {

        System.out.println("Accounts available:");
        showAccounts(currentUser, accountController);

        System.out.println("Transfer from (account):");
        String fromAccount = scanner.nextLine();

        System.out.println("Transfer to (account):");
        String toAccount = scanner.nextLine();

        System.out.println("Write amount:");
        String amount = scanner.nextLine();

        if (fromAccount.isEmpty() ||  toAccount.isEmpty() || amount.isEmpty() ) {
            System.out.println("Input can't be empty. Please try again.");
            return;
        }

        String transactionStatus = transactionController.makeTransaction(fromAccount, toAccount, amount, currentUser);

        System.out.println(transactionStatus);
    }

    public static void showAccountTransactionHistory(UserModel currentUser, Scanner scanner, AccountController accountController, TransactionController transactionController) {

        System.out.println("Accounts available:");
        showAccounts(currentUser, accountController);

        System.out.println("Check history (write account-number):");
        String selectedAccount = scanner.nextLine();

        System.out.println("From: (yyyy-mm-dd)");
        String fromDate = scanner.nextLine();

        System.out.println("To date: (yyyy-mm-dd)");
        String toDate = scanner.nextLine();

        if (selectedAccount.isEmpty() || fromDate.isEmpty() || toDate.isEmpty()) {
            System.out.println("Input can't be empty. Please try again.");
            return;
        }

        List<Map<String, Object>> transactionHistory = transactionController.getTransactionHistory(currentUser,selectedAccount, fromDate, toDate);

        if (transactionHistory == null || transactionHistory.isEmpty()){
            System.out.println("Check your input.");
            return;
        }

        for (Map<String, Object> transaction : transactionHistory) {

            double amount = (double) transaction.get("amount");
            long receiverAccountNumber = Long.parseLong(transaction.get("receiverAccountNumber").toString());
            long senderAccountNumber = Long.parseLong(transaction.get("senderAccountNumber").toString());
            Timestamp time = (Timestamp) transaction.get("time");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedTime = dateFormat.format(time);

            System.out.println("From: " + senderAccountNumber);
            System.out.println("To: " + receiverAccountNumber);
            System.out.println("Amount: " + amount + ", time: " + formattedTime);
            System.out.println("-----------------------------------------------------------");
        }


    }
}
