package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.TransactionManager;
import org.example.model.TransactionModel;
import org.example.model.UserModel;
import org.example.regex.Regex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TransactionController {

    private final Regex regEx;
    private final TransactionModel currentTransaction;
    private final TransactionManager transactionManager;
    public TransactionController(){
        this.regEx = new Regex();
        this.currentTransaction = new TransactionModel();
        this.transactionManager = new TransactionManager();
    }
    public String makeTransaction(String fromAccount, String toAccount, String amount, UserModel currentUser){
        AccountModel sender = new AccountModel();
        AccountModel receiver = new AccountModel();

        if(regEx.RegexNumbers(fromAccount) &&
            regEx.RegexNumbers(toAccount) &&
            regEx.RegexDouble(amount)){

            sender.setAccountNumber(Long.parseLong(fromAccount));
            receiver.setAccountNumber(Long.parseLong(toAccount));

            currentTransaction.setTransactionValue(Double.parseDouble(amount));

            if (transactionManager.makeTransaction(sender, receiver, currentTransaction, currentUser)) {
                return "Transfer successful.";
            }
        }

        return "Something went wrong. Try again";
    }

    public List<Map<String, Object>> getTransactionHistory(UserModel currentUser, String accountNumber, String fromDate, String toDate){
        AccountModel account = new AccountModel();

        if(regEx.RegexNumbers(accountNumber) &&
           regEx.RegexDate(fromDate) &&
           regEx.RegexDate(toDate)) {

            account.setAccountNumber(Long.parseLong(accountNumber));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate parsedFromDate = LocalDate.parse(fromDate, formatter);
            LocalDate parsedToDate = LocalDate.parse(toDate, formatter);

            return transactionManager.getTransactionHistory(currentUser,account,parsedFromDate,parsedToDate);
        }
        return null;
    }
}
