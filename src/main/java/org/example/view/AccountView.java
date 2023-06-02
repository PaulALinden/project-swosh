package org.example.view;

import org.example.controller.AccountController;
import org.example.model.UserModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AccountView {

    public static void showAccountManagementMenu(UserModel currentUser, Scanner scanner, AccountController accountController) {
        System.out.println("1. Add account.");
        System.out.println("2. Remove account.");

        String userChoice = scanner.nextLine();

        switch (userChoice) {
            case "1" -> {
                System.out.println("Write account number:");
                String account = scanner.nextLine();
                System.out.println("Write balance:");
                String balance = scanner.nextLine();

                if (balance.isEmpty() || account.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    return;
                }

                String createAccount = accountController.addNewAccount(currentUser, account, balance);
                System.out.println(createAccount);
            }
            case "2" -> {
                System.out.println("Write account number:");
                String account = scanner.nextLine();

                System.out.println("Write password:");
                String password = scanner.nextLine();

                if (password.isEmpty() || account.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    return;
                }

                String deleteStatus = accountController.deleteAccount(currentUser, account, password);
                System.out.println(deleteStatus);
            }
            default -> System.out.println("Invalid input. try again.");
        }
    }

    public static void showAccounts(UserModel currentUser, AccountController accountController) {
        List<Map<String, Object>> accountList = accountController.getAllAccounts(currentUser);

        String userName = (String) accountList.get(0).get("name");
        Long idNumber = (Long) accountList.get(0).get("identityNumber");
        Timestamp userCreated = (Timestamp) accountList.get(0).get("created");

        System.out.println(userName + ", " + idNumber + ", Created:" + userCreated);
        System.out.println("Accounts available:");

        for (Map<String, Object> account : accountList) {

            if (account.containsKey("accountNumber") && account.containsKey("balance")) {
                long accountNumber = (Long) account.get("accountNumber");
                double balance = (Double) account.get("balance");

                System.out.println("Account: " + accountNumber + " | Balance: " + balance);
            }
        }
        System.out.println("--------------------------------------------");
    }
}
