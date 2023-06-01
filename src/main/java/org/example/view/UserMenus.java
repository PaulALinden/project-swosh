package org.example.view;

import org.example.controller.UserController;
import org.example.model.UserModel;

import java.util.Objects;
import java.util.Scanner;

public class UserMenus{

    public static void showUserSettingsMenu(UserModel user, Scanner scanner, UserController userController) {
        System.out.println("1. Update user");
        System.out.println("2. Delete user");

        String input = scanner.nextLine();

        switch (input) {
            case "1" -> {
                updateUser(user, scanner, userController);
            }
            case "2" -> {
                deleteUser(user, scanner, userController);
            }
            default -> System.out.println("Invalid input");
        }
    }

    private static void updateUser(UserModel user, Scanner scanner, UserController userController) {
        System.out.println("1. Update name");
        System.out.println("2. Update password");
        System.out.println("3. Identity number");

        String updateOption = scanner.nextLine();

        switch (updateOption) {
            case "1" -> {
                System.out.println("Write new name:");
                String name = scanner.nextLine();

                if (name.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    break;
                }

                String updateMessage = userController.updateUserName(user, name);
                System.out.println(updateMessage);
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

                String updateMessage = userController.updatePassword(user, password, newPassword);
                System.out.println(updateMessage);
            }
            case "3" -> {
                System.out.println("Write new Identity number:");
                String identityNumber = scanner.nextLine();

                if (identityNumber.isEmpty()) {
                    System.out.println("Input can't be empty. Please try again.");
                    break;
                }

                String trimmedId = identityNumber.replaceAll("-", "");
                String updateMessage = userController.updateIdentityNumber(user, trimmedId);
                System.out.println(updateMessage);
            }
            default -> System.out.println("Invalid input");
        }
    }

    private static void deleteUser(UserModel user, Scanner scanner, UserController userController) {
        System.out.println("Deleting a user will automatically remove all accounts in the process");
        System.out.println("Are you sure you want to continue? [y]");
        String continueDelete = scanner.nextLine();

        if (continueDelete.equalsIgnoreCase("y")) {
            System.out.println("Write password:");
            String password = scanner.nextLine();

            if (password.isEmpty()) {
                System.out.println("Input can't be empty. Please try again.");
                return;
            }

            String userRemoved = userController.removeUser(user, password);

            System.out.println(Objects.requireNonNullElse(userRemoved, "Something went wrong"));
        }
    }
}
