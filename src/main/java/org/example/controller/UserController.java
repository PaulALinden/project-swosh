package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.UserManager;
import org.example.model.UserModel;
import org.example.regex.Regex;

public class UserController {
    private final UserModel newUser;
    private final UserManager userManager;
    private final AccountModel initialAccount;
    private final Regex regex;

    public UserController() {
        this.newUser = new UserModel();
        this.userManager = new UserManager();
        this.initialAccount = new AccountModel();
        this.regex = new Regex();
    }

    public boolean createNewUser(String name, String identityNumber, String password, String account, String balance) {

        if (regex.RegexIdentityNumber(identityNumber) &&
                regex.RegexLetters(name) &&
                regex.RegexNumbers(account) &&
                regex.RegexDouble(balance)) {

            long parsedIdentityNumber = Long.parseLong(identityNumber);

            newUser.setName(name);
            newUser.setIdentityNumber(parsedIdentityNumber);
            newUser.setPassword(password);
            
            initialAccount.setAccountNumber(Long.parseLong(account));
            initialAccount.setBalance(Double.parseDouble(balance));

            userManager.createUser(newUser, initialAccount);

            return true;
        }
        return false;
    }

    public UserModel loginUser(String identityNumber, String password) {

        if (regex.RegexIdentityNumber(identityNumber)) {

            String idWithoutDashes = identityNumber.replaceAll("-", "");
            long parsedIdentityNumber = Long.parseLong(idWithoutDashes);

            return userManager.userLogin(parsedIdentityNumber, password);
        }
        return null;
    }

    public String updateUserName(UserModel currentUser, String newName) {

        if (regex.RegexLetters(newName)) {
            boolean isUpdated = userManager.updateUserName(currentUser, newName);
            
            if (isUpdated) {
                return "Name is updated.";
            }
        }
        return "Something went wrong. Try again";
    }
    public String updatePassword(UserModel currentUser, String password, String newPassword) {
        
        boolean isUpdated = userManager.updatePassword(password, newPassword, currentUser);
        
        if (isUpdated) {
            return "Password is updated.";
        }

        return "Something went wrong. Try again";
    }
    public String updateIdentityNumber(UserModel currentUser, String newIdNumber) {
        
        if (regex.RegexIdentityNumber(newIdNumber)) {
            
            boolean isUpdated = userManager.updateIdentityNumber(Long.parseLong(newIdNumber), currentUser);
            
            if (isUpdated) {
                return "Identity number is updated";
            } else {
                return "Something went wrong";
            }
            
        }
        return "Something went wrong. Try again";
    }

    public String deleteUserAccount(UserModel currentUser, String password) {

        boolean idDeleted = userManager.deleteUser(password, currentUser);

        if (idDeleted) {
            return "User successfully deleted";
        }

        return "Something went wrong. Try again";
    }

    public boolean logoutUser(UserModel currentUser){
        return userManager.setUserOffline(currentUser);
    }
}
