package org.example.controller;

import org.example.model.AccountManager;
import org.example.model.AccountModel;
import org.example.model.UserModel;
import org.example.regex.Regex;

import java.util.List;
import java.util.Map;

public class AccountController {

   private final Regex regex;
    private final AccountModel accountModel;
    private final AccountManager accountManager;
    public AccountController() {
        this.regex = new Regex();
        this.accountModel = new AccountModel();
        this.accountManager = new AccountManager();
    }
    public String addNewAccount(UserModel currentUser, String account, String balance){

        if(regex.RegexDouble(balance) && regex.RegexNumbers(account)) {

            accountModel.setAccountNumber(Long.parseLong(account));
            accountModel.setBalance(Double.parseDouble(balance));

            boolean isCreated = accountManager.createAccount(accountModel, currentUser);

            if (isCreated) {
                return "Account created.";
            }
        }
        return "Something went wring. Try again";
    }

    public String deleteAccount(UserModel currentUser, String account, String password){

        if (regex.RegexNumbers(account)) {

            accountModel.setAccountNumber(Long.parseLong(account));
            accountModel.setUserId(currentUser.getId());

            boolean isDeleted = accountManager.deleteAccount(currentUser, accountModel, password);

            if (isDeleted) {
                return "Account deleted";
            }
        }
        return "Something went wrong";
    }

    public List<Map<String, Object>> getAllAccounts(UserModel currentUser){

        accountModel.setUserId(currentUser.getId());

        return accountManager.getAccountsByUserId(accountModel);
    }
}
