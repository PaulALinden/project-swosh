package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.controller.UserController;
import org.example.model.AccountModel;
import org.example.model.TransactionModel;
import org.example.model.UserModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class SubMenu {
    private static final Scanner scanner = new Scanner(System.in);
    public static void userMenu(UserModel user) {
        while (true) {
            System.out.println("1. Check accounts");
            System.out.println("2. Make transfer");
            System.out.println("3. Settings");
            System.out.println("10.Back");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> System.out.println("something");
                case "2" -> transfer(user);
                case "3" -> {boolean isIn = settings(user);
                    if (isIn){
                        return;
                    }
                }
                case "10" -> {
                    return;
                }
                default -> System.out.println("Invalid input");
            }
        }
    }
    public static boolean settings(UserModel user) {
        System.out.println("1. User settings");
        System.out.println("2. Account management");

        String input = scanner.nextLine();

        switch (input) {
            case "1" -> {
                return userSettings(user);
            }
            case "2" -> accountManagement(user);
            default -> System.out.println("Invalid input");
        }
        return false;
    }
    public static boolean userSettings(UserModel user) {
        System.out.println("1. Update user");
        System.out.println("2. Delete user");

        String input = scanner.nextLine();

        if (input.equals("1")) {
            System.out.println("1. Update name:");
            System.out.println("2. Update password:");
            System.out.println("3. Identity number:");
            String updateOption = scanner.nextLine();

            ArrayList<String> updateValues = new ArrayList<>();
            UserController userController = new UserController();

            switch (updateOption) {
                case "1" -> {
                    System.out.println("Write new name:");
                    String name = scanner.nextLine();

                    updateValues.add(name);

                    String update = userController.updateUser(user, updateOption, updateValues);
                    System.out.println(update);
                }
                case "2" -> {

                    System.out.println("Write password:");
                    String password = scanner.nextLine();

                    System.out.println("Write new password:");
                    String newPassword = scanner.nextLine();

                    updateValues.add(password);
                    updateValues.add(newPassword);

                    String update = userController.updateUser(user, updateOption, updateValues);
                    System.out.println(update);
                }
                case "3" -> {
                    System.out.println("Write new Identity number:");
                    String identityNumber = scanner.nextLine();

                    String trimmedId = identityNumber.replaceAll("-", "");
                    updateValues.add(trimmedId);

                    String update = userController.updateUser(user, updateOption, updateValues);
                    System.out.println(update);
                }
            }

        }
        else if (input.equals("2")) {
            System.out.println("Deleting user will automatically remove all account specifics and transaction history");
            System.out.println("Are you sure you want to continue:");
            System.out.println("[y/n]");
            String continueDelete = scanner.nextLine();

            if (Objects.equals(continueDelete, "y") || Objects.equals(continueDelete, "Y")){
                System.out.println("Write password:");
                String password = scanner.nextLine();

                UserController userController = new UserController();
                String userRemoved = userController.removeUser(user, password);

                if (userRemoved != null) {
                    System.out.println(userRemoved);
                    return true;
                }
                else{
                    System.out.println("Something went wrong");
                }
            }
        }
        else {
            System.out.println("Invalid input");
        }
        return false;
    }
    public static void accountManagement(UserModel user){
        System.out.println("1.Add account");
        System.out.println("2.Remove account");

        String input = scanner.nextLine();

        AccountController accountController = new AccountController();
        long account;

        switch (input){
            case "1":
                System.out.println("Write account number:");
                account = Long.parseLong(scanner.nextLine());
                System.out.println("Write balance");
                long balance = Long.parseLong(scanner.nextLine());

                String createAccount = accountController.addNewAccount(user, account, balance);

                System.out.println(createAccount);
            case "2":

                System.out.println("Write account number:");
                account = Long.parseLong(scanner.nextLine());

                System.out.println("Write Password:");
                String password = scanner.nextLine();

                String deleteAccount = accountController.removeAccount(user,account, password);

                System.out.println(deleteAccount);
        }
    }
    public static void transfer(UserModel user){
        TransactionController transactionController = new TransactionController();

        System.out.println("Transfer from: (account)");
        String fromAccount = scanner.nextLine();

        System.out.println("Write Amount:");
        String amount = scanner.nextLine();

        System.out.println("Transfer to: (account)");
        String toAccount = scanner.nextLine();

        String transfer = transactionController.makeTransfer(fromAccount,toAccount, amount, user.getId());
        System.out.println(transfer);
    }

}

