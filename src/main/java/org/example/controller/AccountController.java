package org.example.controller;

import org.example.model.AccountManager;
import org.example.model.AccountModel;
import org.example.model.UserModel;
import org.example.regex.RegEx;

import java.util.List;
import java.util.Map;

public class AccountController {

   private final RegEx regex;
    private final AccountModel accountModel;
    private final AccountManager accountManager;
    public AccountController() {
        this.regex = new RegEx();
        this.accountModel = new AccountModel();
        this.accountManager = new AccountManager();
    }
    public String addNewAccount(UserModel user, String account, String balance){

        if(regex.RegExNumbersDouble(balance) && regex.RegExNumbersLong(account)) {

            accountModel.setAccountNumber(Long.parseLong(account));
            accountModel.setBalance(Double.parseDouble(balance));

            boolean isCreated = accountManager.createAccount(accountModel, user);

            if (isCreated) {
                return "Account created";
            }
        }
        return "Incorrect account number";
    }

    public String removeAccount(UserModel user, String account, String password){

        if (regex.RegExNumbersLong(account)) {

            accountModel.setAccountNumber(Long.parseLong(account));
            accountModel.setUserId(user.getId());

            boolean isDeleted = accountManager.deleteAccount(user, accountModel, password);

            if (isDeleted) {
                return "Account deleted";
            }
        }
        return "Something went wrong";
    }

    public List<Map<String, Object>> getUsersAccounts(UserModel user){

        accountModel.setUserId(user.getId());

        return accountManager.getAccountsFromUser(accountModel);
    }
}
