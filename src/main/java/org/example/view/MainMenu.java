package org.example.view;

import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Scanner;

public class MainMenu {

    static Scanner scanner = new Scanner(System.in);

    public static void mainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Welcome to Swosh!");
            System.out.println("Choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Create a new user account");
            System.out.println("10. Quit");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> login();
                case "2" -> newUser();
                case "10" -> {
                    System.out.println("Shutting down");
                    running = false;
                }
                default ->
                        System.out.println("Wrong input... Please type the number corresponding to the desired option and press enter.");
            }
        }
        scanner.close();
    }

    public static void newUser() {
        UserController newUserController = new UserController();
        boolean creatingUser = true;

        while (creatingUser) {
            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Enter the following information to create a new user account:");
            System.out.println("First name:");
            String name = scanner.nextLine();
            System.out.println("Identity number (only numbers):");
            String identityNumber = scanner.nextLine();
            System.out.println("Password:");
            String password = scanner.nextLine();
            System.out.println("Bank account number:");
            String bankAccount = scanner.nextLine();
            System.out.println("Balance:");
            String balance = scanner.nextLine();

            System.out.println("Name: " + name);
            System.out.println("ID: " + identityNumber);
            System.out.println("Password: " + password);
            System.out.println("Bank account: " + bankAccount);
            System.out.println("Balance: " + balance);

            String trimmedId = identityNumber.replaceAll("-", "");

            boolean isCreated = newUserController.newUser(name, trimmedId, password, bankAccount, balance);

            if (isCreated) {
                System.out.println("New user created");
                creatingUser = false;
            } else {
                System.out.println("Failed to create user. Please try again.");
            }
        }
    }

    public static void login() {
        System.out.println("__Login__");
        System.out.println("Identity number:");
        String identity = scanner.nextLine();

        System.out.println("Password:");
        String password = scanner.nextLine();

        UserController userController = new UserController();
        UserModel user = userController.loginController(identity, password);

        if (user == null) {
            System.out.println("Wrong username or password");
        } else {
            System.out.println("Welcome, " + user.getName());
            SubMenu.showUserMenu(user);
        }
    }
}
