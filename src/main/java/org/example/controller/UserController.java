package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.UserManager;
import org.example.model.UserModel;
import org.example.regex.RegEx;

public class UserController {
    private final UserModel user;
    private final UserManager userManagement;
    private final AccountModel firstAccount;
    private final RegEx regEx;

    public UserController() {
        this.user = new UserModel();
        this.userManagement = new UserManager();
        this.firstAccount = new AccountModel();
        this.regEx = new RegEx();
    }

    public boolean newUser(String name, String identityNumber, String password, String account, String balance) {

        if (regEx.RegExIdentityNumber(identityNumber) &&
                regEx.RegExLetters(name) &&
                regEx.RegExNumbersLong(account) &&
                regEx.RegExNumbersDouble(balance)) {

            long parsedIdentityNumber = Long.parseLong(identityNumber);

            user.setName(name);
            user.setIdentityNumber(parsedIdentityNumber);
            user.setPassword(password);
            firstAccount.setAccountNumber(Long.parseLong(account));
            firstAccount.setBalance(Double.parseDouble(balance));

            userManagement.createUser(user, firstAccount);

            return true;
        }
        return false;
    }

    public UserModel loginController(String identityNumber, String password) {

        if (regEx.RegExIdentityNumber(identityNumber)) {

            String trim = identityNumber.replaceAll("-", "");
            long parsedIdentityNumber = Long.parseLong(trim);

            return userManagement.userLogin(parsedIdentityNumber, password);
        }
        return null;
    }

    public String updateUserName(UserModel user, String name) {
        if (regEx.RegExLetters(name)) {
            boolean nameIsUpdated = userManagement.updateUserName(user, name, user.getId());
            if (nameIsUpdated) {
                return "Name is updated";
            } else {
                return "Something went wrong";
            }
        } else {
            return "Wrong input";
        }
    }
    public String updatePassword(UserModel user, String currentPassword, String newPassword) {
        boolean passwordIsUpdated = userManagement.updatePassword(currentPassword, newPassword, user);
        if (passwordIsUpdated) {
            return "Password is updated";
        } else {
            return "Something went wrong";
        }
    }
    public String updateIdentityNumber(UserModel user, String identityNumber) {
        if (regEx.RegExIdentityNumber(identityNumber)) {
            boolean identityNumbIsUpdated = userManagement.updateIdentityNumber(Long.parseLong(identityNumber), user);
            if (identityNumbIsUpdated) {
                return "Identity number is updated";
            } else {
                return "Something went wrong";
            }
        } else {
            return "Wrong input";
        }
    }

    public String removeUser(UserModel user, String password) {

        boolean userIsRemoved = userManagement.deleteUser(password, user);

        if (userIsRemoved) {
            return "User successfully removed";
        }
        return null;
    }

    public void logoutController(UserModel user){
        userManagement.setUserOffline(user);
    }
}
