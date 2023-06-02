package org.example.view;

import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Scanner;

public class UserView {

    public static void showUserManagementMenu(UserModel currentUser, Scanner scanner, UserController userController) {
        System.out.println("1. Update user");
        System.out.println("2. Delete user");

        String userChoice = scanner.nextLine();

        switch (userChoice) {
            case "1" -> {
                updateUser(currentUser, scanner, userController);
            }
            case "2" -> {
                deleteUser(currentUser, scanner, userController);
            }
            default -> System.out.println("Invalid input. try again.");
        }
    }

    private static void updateUser(UserModel currentUser, Scanner scanner, UserController userController) {
        System.out.println("1. Update name");
        System.out.println("2. Update password");
        System.out.println("3. Identity number");

        String userChoice = scanner.nextLine();

        switch (userChoice) {
            case "1" -> {
                System.out.println("Write new name:");
                String name = scanner.nextLine();

                if (name.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    break;
                }

                String updateStatus = userController.updateUserName(currentUser, name);
                System.out.println(updateStatus);
            }
            case "2" -> {
                System.out.println("Write password:");
                String password = scanner.nextLine();

                System.out.println("Write new password:");
                String newPassword = scanner.nextLine();

                if (password.isEmpty() || newPassword.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    break;
                }

                String updateStatus = userController.updatePassword(currentUser, password, newPassword);
                System.out.println(updateStatus);
            }
            case "3" -> {
                System.out.println("Write new Identity number:");
                String identityNumber = scanner.nextLine();

                if (identityNumber.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    break;
                }

                String idWithoutDashes = identityNumber.replaceAll("-", "");
                String updateStatus = userController.updateIdentityNumber(currentUser, idWithoutDashes);
                System.out.println(updateStatus);
            }
            default -> System.out.println("Invalid input. try again.");
        }
    }

    private static void deleteUser(UserModel currentUser, Scanner scanner, UserController userController) {
        System.out.println("Deleting a user will automatically remove all accounts in the process.");
        System.out.println("Are you sure you want to continue? [y].");
        String continueDelete = scanner.nextLine();

        if (continueDelete.equalsIgnoreCase("y")) {
            System.out.println("Write password:");
            String password = scanner.nextLine();

            if (password.isEmpty()) {
                System.out.println("Input can't be empty. Please try again.");
                return;
            }

            String deleteStatus = userController.deleteUserAccount(currentUser, password);

            System.out.println(deleteStatus);
        }
    }
}
