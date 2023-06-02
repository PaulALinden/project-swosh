package org.example.view;

import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Scanner;

public class MainMenu {

    static Scanner scanner = new Scanner(System.in);
    protected static final UserController USERCONTROLLER = new UserController();
    public static void displayMainMenu() {

        while (true) {
            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Welcome to Swosh!");
            System.out.println();
            System.out.println("Choose option:");
            System.out.println("1. Login.");
            System.out.println("2. Create a new user account.");
            System.out.println("10. Quit.");

            String userChoice = scanner.nextLine();

            switch (userChoice) {
                case "1" -> login();
                case "2" -> createNewUser();
                case "10" -> {
                    System.out.println("Shutting down.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid input. try again.");
            }
        }
    }

    public static void createNewUser() {


            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Enter the following information to create a new user account:");

            System.out.println("First name: (a-z)");
            String name = scanner.nextLine();

            System.out.println("Identity number (yyyymmdd-xxxx):");
            String idNumber = scanner.nextLine();

            System.out.println("Password:");
            String password = scanner.nextLine();

            System.out.println("Bank-account number: (0-9)");
            String accountNumber = scanner.nextLine();

            System.out.println("Balance: (0-9 & 0.0-0.9)");
            String balance = scanner.nextLine();

            if (name.isEmpty() || idNumber.isEmpty() || password.isEmpty() || accountNumber.isEmpty() || balance.isEmpty()) {
                System.out.println("Input can't be empty. Please try again.");
                return;
            }

            String idWithoutDashes = idNumber.replaceAll("-", "");

            boolean isCreated = USERCONTROLLER.createNewUser(name.toLowerCase(), idWithoutDashes, password, accountNumber, balance);

            if (isCreated) {
                System.out.println("New user created.");
            } else {
                System.out.println("Failed to create user. Please try again.");
            }
    }

    public static void login() {
        System.out.println("__Login__");

        System.out.println("Identity number: (yyyymmdd-xxxx)");
        String idNumber = scanner.nextLine();

        System.out.println("Password:");
        String password = scanner.nextLine();

        if (idNumber.isEmpty() || password.isEmpty()) {
            System.out.println("Input can't be empty. Please try again.");
            return;
        }

        UserModel currentUser = USERCONTROLLER.loginUser(idNumber, password);

        if (currentUser == null) {
            System.out.println("Wrong username or password.");
        } else {
            String name = currentUser.getName();

            System.out.println("Welcome, " + name.substring(0, 1).toUpperCase() + name.substring(1));
            SubMenu.showLoggedInMenu(currentUser);
        }
    }
}
