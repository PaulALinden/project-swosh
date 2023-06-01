package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.TransactionManager;
import org.example.model.TransactionModel;
import org.example.model.UserModel;
import org.example.regex.RegEx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TransactionController {

    private final RegEx regEx;
    private final TransactionModel transactionModel;
    private final TransactionManager transactionManager;
    public TransactionController(){
        this.regEx = new RegEx();
        this.transactionModel = new TransactionModel();
        this.transactionManager = new TransactionManager();
    }
    public String makeTransfer(String fromAccount, String toAccount, String amount,UserModel user){
        AccountModel senderAccount = new AccountModel();
        AccountModel receiverAccount = new AccountModel();

        if(regEx.RegExNumbersLong(fromAccount) &&
            regEx.RegExNumbersLong(toAccount) &&
            regEx.RegExNumbersDouble(amount)){

            senderAccount.setAccountNumber(Long.parseLong(fromAccount));
            receiverAccount.setAccountNumber(Long.parseLong(toAccount));
            transactionModel.setTransactionValue(Double.parseDouble(amount));

            if (transactionManager.makeTransfer(senderAccount, receiverAccount, transactionModel, user)) {
                return "Transfer successful.";
            }
            return "Wrong account or amount,";
        }
    return "Wrong input use numbers only.";
    }

    public List<Map<String, Object>> getTransactions(UserModel user, String accountNumber, String startDateTime, String endDateTime){
        AccountModel account = new AccountModel();

        if(regEx.RegExNumbersLong(accountNumber) &&
           regEx.RegExNumbersDate(startDateTime) &&
           regEx.RegExNumbersDate(endDateTime)) {

            account.setAccountNumber(Long.parseLong(accountNumber));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate start = LocalDate.parse(startDateTime, formatter);
            LocalDate end = LocalDate.parse(endDateTime, formatter);
            return transactionManager.getTransactionHistory(user,account,start,end);
        }
        return null;
    }
}
