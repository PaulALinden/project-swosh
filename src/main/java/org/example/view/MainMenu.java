package org.example.view;

import org.example.controller.UserController;
import org.example.model.UserModel;

import java.sql.SQLException;
import java.util.Scanner;

public class MainMenu {

    static Scanner scanner = new Scanner(System.in);

    public static void mainMenu() throws SQLException {
        while (true) {
            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Welcome to Swosh!");
            System.out.println("Chose a option:");
            System.out.println("1.Login");
            System.out.println("2.Create new user account");
            System.out.println("10.Quit");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> login();
                case "2" -> newUser();
                case "10" -> {
                    System.out.println("Shutting down");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Wrong input...type the number of option then press enter.");
            }
        }
    }

    public static void newUser(){
        UserController newUserController = new UserController();
        while (true) {

            System.out.println("~~~~~~~~~~~~~~~~~~");
            System.out.println("Write firstname:");
            String name = scanner.nextLine();
            System.out.println("Write identity number(only numbers):");
            String identityNumber = scanner.nextLine();
            System.out.println("Write password:");
            String password = scanner.nextLine();
            System.out.println("Write bank account number:");
            long bankAccount = Long.parseLong(scanner.nextLine());
            System.out.println("Write balance:");
            long balance = Long.parseLong(scanner.nextLine());

            System.out.println("Name: " + name);
            System.out.println("ID: " + identityNumber);
            System.out.println("Pass: " + password);
            System.out.println("Bank account: " + bankAccount);
            System.out.println("Balance: " + balance);

            String trimmedId = identityNumber.replaceAll("-", "");

            boolean isCreated = newUserController.newUser(name, trimmedId, password, bankAccount, balance);

            if (isCreated){
                System.out.println("New user created");
                return;
            }
        }
    }

    public static void login() throws SQLException {

        System.out.println("__Login__");
        System.out.println("Identity number:");
        String identity = scanner.nextLine();

        System.out.println("Password:");
        String password = scanner.nextLine();

        UserController userController = new UserController();
        UserModel user = userController.loginController(identity,password);

        if (user == null){
            System.out.println("Wrong username or password");
        }
        else {
            System.out.println("Welcome " + user.getName());
            SubMenu.userMenu(user);
        }
    }
}