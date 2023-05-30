package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.UserModel;
import org.example.regex.RegEx;

public class AccountController {

   private final RegEx regex;
    private final AccountModel accountModel;
    public AccountController() {
        this.regex = new RegEx();
        this.accountModel = new AccountModel();
    }
    public String addNewAccount(UserModel user, long account, long balance){
        boolean isCreated = accountModel.createAccount(account, balance, user.getId());

        if (isCreated){
            return "Account created";
        }

        return "Incorrect account number";
    }

    public String removeAccount(UserModel user, long account, String password){

        boolean isDeleted = accountModel.deleteAccount(user.getId(), user.getPassword(),account, password);

        if(isDeleted){
            return "Account deleted";
        }

        return "Something went wrong";
    }

}
