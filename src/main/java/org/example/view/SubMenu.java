package org.example.view;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.controller.UserController;
import org.example.model.UserModel;

public class SubMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserController userController = new UserController();
    private static final AccountController accountController = new AccountController();
    private static final TransactionController transactionController = new TransactionController();

    public static void showUserMenu(UserModel user) {
        while (true) {
            System.out.println("1. Check accounts");
            System.out.println("2. Transfers");
            System.out.println("3. Settings");
            System.out.println("10. Logout");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showAccounts(user);
                case "2" -> showTransactionMenu(user);
                case "3" -> {
                    boolean exit = showSettingsMenu(user);
                    if (exit) {
                        return;
                    }
                }
                case "10" -> {
                    logout(user);
                    return;
                }
                default -> System.out.println("Invalid input");
            }
        }
    }

    public static boolean showSettingsMenu(UserModel user) {
        System.out.println("1. User settings");
        System.out.println("2. Account management");

        String input = scanner.nextLine();

        switch (input) {
            case "1" -> {
                return showUserSettingsMenu(user);
            }
            case "2" -> showAccountManagementMenu(user);
            default -> System.out.println("Invalid input");
        }
        return false;
    }

    public static boolean showUserSettingsMenu(UserModel user) {
        System.out.println("1. Update user");
        System.out.println("2. Delete user");

        String input = scanner.nextLine();

        if (input.equals("1")) {
            System.out.println("1. Update name:");
            System.out.println("2. Update password:");
            System.out.println("3. Identity number:");
            String updateOption = scanner.nextLine();

            String updateMessage;
            switch (updateOption) {
                case "1" -> {
                    System.out.println("Write new name:");
                    String name = scanner.nextLine();
                    updateMessage = userController.updateUserName(user, name);
                    System.out.println(updateMessage);
                }
                case "2" -> {
                    System.out.println("Write password:");
                    String password = scanner.nextLine();
                    System.out.println("Write new password:");
                    String newPassword = scanner.nextLine();
                    updateMessage = userController.updatePassword(user, password, newPassword);
                    System.out.println(updateMessage);
                }
                case "3" -> {
                    System.out.println("Write new Identity number:");
                    String identityNumber = scanner.nextLine();
                    String trimmedId = identityNumber.replaceAll("-", "");
                    updateMessage = userController.updateIdentityNumber(user, trimmedId);
                    System.out.println(updateMessage);
                }
                default -> System.out.println("Invalid input");
            }
        } else if (input.equals("2")) {
            System.out.println("Deleting a user will automatically remove all accounts in the process");
            System.out.println("Are you sure you want to continue? [y/n]");
            String continueDelete = scanner.nextLine();

            if (continueDelete.equalsIgnoreCase("y")) {
                System.out.println("Write password:");
                String password = scanner.nextLine();

                String userRemoved = userController.removeUser(user, password);

                if (userRemoved != null) {
                    System.out.println(userRemoved);
                    return true;
                } else {
                    System.out.println("Something went wrong");
                }
            }
        } else {
            System.out.println("Invalid input");
        }
        return false;
    }

    public static void showTransactionMenu(UserModel user) {
        System.out.println("1. Make transaction");
        System.out.println("2. Transaction history account");

        String input = scanner.nextLine();

        switch (input) {
            case "1" -> makeTransaction(user);
            case "2" -> transactionHistory(user);
            default -> System.out.println("Invalid input");
        }
    }

    //------------------------------------------------------------------------
    public static void showAccountManagementMenu(UserModel user) {
        System.out.println("1. Add account");
        System.out.println("2. Remove account");

        String input = scanner.nextLine();
        String account;

        switch (input) {
            case "1" -> {
                System.out.println("Write account number:");
                account = scanner.nextLine();
                System.out.println("Write balance:");
                String balance = scanner.nextLine();
                String createAccount = accountController.addNewAccount(user, account, balance);
                System.out.println(createAccount);
            }
            case "2" -> {
                System.out.println("Write account number:");
                account = scanner.nextLine();
                System.out.println("Write password:");
                String password = scanner.nextLine();
                String deleteAccount = accountController.removeAccount(user, account, password);
                System.out.println(deleteAccount);
            }
            default -> System.out.println("Invalid input");
        }
    }

    public static void makeTransaction(UserModel user) {

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
        String fromAccount = scanner.nextLine();

        System.out.println("Write amount:");
        String amount = scanner.nextLine();

        System.out.println("Transfer to (account):");
        String toAccount = scanner.nextLine();

        String transfer = transactionController.makeTransfer(fromAccount, toAccount, amount, user.getId());

        System.out.println(transfer);
    }

    public static void transactionHistory(UserModel user) {
        List<Map<String, Object>> accounts = accountController.getUsersAccounts(user);

        System.out.println("Accounts available:");
        for (Map<String, Object> account : accounts) {

            Long accountNumber = (Long) account.get("accountNumber");
            Double balance = (Double) account.get("balance");

            System.out.println("Account: " + accountNumber + " | Balance: " + balance);
        }

        System.out.println("Check history of account (write account):");
        String account = scanner.nextLine();

        System.out.println("From:");
        String from = scanner.nextLine();

        System.out.println("To date:");
        String to = scanner.nextLine();

        List<Map<String, Object>> history = transactionController.getTransactions(user, account, from, to);

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

    public static void logout(UserModel user) {
        userController.logoutController(user.getId());
    }

    private static void showAccounts(UserModel user) {
        List<Map<String, Object>> accounts = accountController.getUsersAccounts(user);

        String name = (String) accounts.get(0).get("name");
        Long id = (Long) accounts.get(0).get("identityNumber");
        Timestamp created = (Timestamp) accounts.get(0).get("created");

        System.out.println(name + ", " + id + ", Created:" + created);
        System.out.println("Accounts available:");

        for (Map<String, Object> account : accounts) {
            if (account.containsKey("accountNumber") && account.containsKey("balance")) {
                long accountNumber = (Long) account.get("accountNumber");
                double balance = (Double) account.get("balance");

                System.out.println("Account: " + accountNumber + " | Balance: " + balance);
            }
        }
        System.out.println("--------------------------------------------");
    }
}



