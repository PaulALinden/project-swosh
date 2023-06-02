package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Scanner;

import static org.example.view.AccountView.showAccountManagementMenu;
import static org.example.view.AccountView.showAccounts;
import static org.example.view.TransactionView.showTransactionMenu;
import static org.example.view.UserView.showUserManagementMenu;

public class SubMenu {
    protected static final Scanner SCANNER = new Scanner(System.in);
    protected static final UserController USER_CONTROLLER = new UserController();
    protected static final AccountController ACCOUNT_CONTROLLER = new AccountController();
    protected static final TransactionController TRANSACTION_CONTROLLER = new TransactionController();

    public static void showLoggedInMenu(UserModel currentUser) {
        while (true) {
            System.out.println("1. Check accounts");
            System.out.println("2. Transfers");
            System.out.println("3. Settings");
            System.out.println("10. Logout");

            String userChoice = SCANNER.nextLine();

            switch (userChoice) {
                case "1" -> showAccounts(currentUser, ACCOUNT_CONTROLLER);
                case "2" -> showTransactionMenu(currentUser, SCANNER, ACCOUNT_CONTROLLER, TRANSACTION_CONTROLLER);
                case "3" -> showSettingsMenu(currentUser);

                case "10" -> {
                    if(USER_CONTROLLER.logoutUser(currentUser)){
                        return;
                    }
                }
                default -> System.out.println("Invalid input");
            }
        }
    }

    public static void showSettingsMenu(UserModel currentUser) {

        System.out.println("1. User settings");
        System.out.println("2. Account management");

        String userChoice = SCANNER.nextLine();

        switch (userChoice) {
            case "1" -> showUserManagementMenu(currentUser, SCANNER, USER_CONTROLLER);
            case "2" -> showAccountManagementMenu(currentUser, SCANNER, ACCOUNT_CONTROLLER);default -> System.out.println("Invalid input. Try again.");
        }
    }
}



