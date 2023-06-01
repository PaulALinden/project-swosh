package org.example.view;

import org.example.controller.AccountController;
import org.example.controller.TransactionController;
import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Scanner;

import static org.example.view.AccountMenus.showAccountManagementMenu;
import static org.example.view.AccountMenus.showAccounts;
import static org.example.view.TransactionMenus.showTransactionMenu;
import static org.example.view.UserMenus.showUserSettingsMenu;

public class SubMenu {
    protected static final Scanner SCANNER = new Scanner(System.in);
    protected static final UserController USER_CONTROLLER = new UserController();
    protected static final AccountController ACCOUNT_CONTROLLER = new AccountController();
    protected static final TransactionController TRANSACTION_CONTROLLER = new TransactionController();

    public static void showLoggedInMenu(UserModel user) {
        while (true) {
            System.out.println("1. Check accounts");
            System.out.println("2. Transfers");
            System.out.println("3. Settings");
            System.out.println("10. Logout");

            String input = SCANNER.nextLine();

            switch (input) {
                case "1" -> showAccounts(user, ACCOUNT_CONTROLLER);
                case "2" -> showTransactionMenu(user, SCANNER, ACCOUNT_CONTROLLER, TRANSACTION_CONTROLLER);
                case "3" -> showSettingsMenu(user);

                case "10" -> {
                    USER_CONTROLLER.logoutController(user);
                    return;
                }
                default -> System.out.println("Invalid input");
            }
        }
    }

    public static void showSettingsMenu(UserModel user) {
        System.out.println("1. User settings");
        System.out.println("2. Account management");

        String input = SCANNER.nextLine();

        switch (input) {
            case "1" -> showUserSettingsMenu(user, SCANNER, USER_CONTROLLER);
            case "2" -> showAccountManagementMenu(user, SCANNER, ACCOUNT_CONTROLLER);

            default -> System.out.println("Invalid input. Try again.");
        }
    }
}



