package org.example.controller;

import org.example.model.TransactionManager;
import org.example.model.TransactionModel;
import org.example.regex.RegEx;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public String makeTransfer(String fromAccount, String toAccount, String amount, int userId){

        if(regEx.RegExNumbersLong(Long.parseLong(fromAccount)) &&
            regEx.RegExNumbersLong(Long.parseLong(toAccount)) &&
            regEx.RegExNumbersDouble(Double.parseDouble(amount))){

            long parsedFromAccount = Long.parseLong(fromAccount);
            long parsedToAccount = Long.parseLong(toAccount);
            double parsedAmount = Double.parseDouble(amount);

            if (transactionManager.makeTransfer(parsedFromAccount, parsedToAccount, parsedAmount, userId)) {
                return "Transfer successful";
            }
            return "Wrong account or amount";
        }
    return "Wrong input use numbers only";
    }

    public List<Map<String, Object>> getTransactions(String account, String startDateTime, String endDateTime){

        if(regEx.RegExNumbersLong(Long.parseLong(account)) &&
                regEx.RegExNumbersLong(Long.parseLong(startDateTime.replace("-",""))) &&
                regEx.RegExNumbersLong(Long.parseLong(endDateTime.replace("-","")))) {

            long parsedAcc = Long.parseLong(account);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate start = LocalDate.parse(startDateTime, formatter);
            LocalDate end = LocalDate.parse(endDateTime, formatter);
            return transactionManager.getTransactionHistory(parsedAcc,start,end);
        }
        return null;
    }
}
