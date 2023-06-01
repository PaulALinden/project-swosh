package org.example.view;

import org.example.controller.AccountController;
import org.example.model.UserModel;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AccountMenus {

    public static void showAccountManagementMenu(UserModel user, Scanner scanner, AccountController accountController) {
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

                if (balance.isEmpty() || account.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    return;
                }

                String createAccount = accountController.addNewAccount(user, account, balance);
                System.out.println(createAccount);
            }
            case "2" -> {
                System.out.println("Write account number:");
                account = scanner.nextLine();
                System.out.println("Write password:");
                String password = scanner.nextLine();

                if (password.isEmpty() || account.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    return;
                }

                String deleteAccount = accountController.removeAccount(user, account, password);
                System.out.println(deleteAccount);
            }
            default -> System.out.println("Invalid input");
        }
    }

    public static void showAccounts(UserModel user, AccountController accountController) {
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
