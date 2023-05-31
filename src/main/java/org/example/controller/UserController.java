package org.example.controller;

import org.example.model.UserManagement;
import org.example.model.UserModel;
import org.example.regex.RegEx;

import java.util.ArrayList;
import java.util.Objects;

public class UserController {
    private final UserManagement userManagement;
    private final RegEx regEx;

    public UserController() {
        this.userManagement = new UserManagement();
        this.regEx = new RegEx();
    }

    public boolean newUser(String name, String identityNumber, String password, long account, long balance) {

        boolean checkedIdentityNumber = regEx.RegExIdentityNumber(identityNumber);
        boolean checkedName = regEx.RegExLetters(name);
        boolean checkedAccount = regEx.RegExNumbersLong(account);
        boolean checkedBalance = regEx.RegExNumbersLong(balance);

        if (checkedIdentityNumber && checkedName && checkedAccount && checkedBalance) {
            System.out.println("Valid input");

            long parsedIdentityNumber = Long.parseLong(identityNumber);
            userManagement.createUser(name, parsedIdentityNumber, password, account, balance);
        } else {
            return false;
        }
        return true;
    }

    public UserModel loginController(String identity, String password) {

        if (regEx.RegExIdentityNumber(identity)) {

            String trim = identity.replaceAll("-", "");
            long parsedIdentityNumber = Long.parseLong(trim);

            return userManagement.verifyLogin(parsedIdentityNumber, password);
        }
        return null;
    }

    public String updateUser(UserModel user, String updateOption, ArrayList<String> updateValues) {

        if (Objects.equals(updateOption, "1")) {

            if(regEx.RegExLetters(updateValues.get(0))) {

                boolean nameIsUpdated = userManagement.updateUserName(updateValues.get(0), user.getId());
                if (nameIsUpdated) {
                    return "Name is updated";
                } else {
                    return "Something went wrong";
                }
            }else {
                return "Wrong input";
            }

        } else if (Objects.equals(updateOption, "2")) {

            boolean passwordIsUpdated = userManagement.updatePassword(updateValues.get(0), updateValues.get(1), user.getId(), user.getPassword());
            if (passwordIsUpdated) {
                return "Pass is updated";
            } else {
                return "Something went wrong";
            }

        } else if (Objects.equals(updateOption, "3")) {

            if (regEx.RegExIdentityNumber(updateValues.get(0))) {
                boolean identityNumbIsUpdated = userManagement.updateIdentityNumber(Long.parseLong(updateValues.get(0)), user.getId());

                if (identityNumbIsUpdated) {
                    return "Identity number is updated";
                } else {
                    return "Something went wrong";
                }
            }else {
                return "Wrong input";
            }
        }
        return null;
    }

    public String removeUser(UserModel user, String password) {

        boolean userIsRemoved = userManagement.deleteUser(password, user.getPassword(), user.getId());

        if (userIsRemoved) {
            return "User successfully removed";
        }

        return null;
    }

    public void logoutController(int id){
        userManagement.setUserOffline(id);
    }
}
