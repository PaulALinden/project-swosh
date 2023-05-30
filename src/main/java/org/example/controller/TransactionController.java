package org.example.controller;

import org.example.model.AccountModel;
import org.example.model.TransactionModel;
import org.example.regex.RegEx;

public class TransactionController {

    private final RegEx regEx;
    private final TransactionModel transactionModel;
    public TransactionController(){
        this.regEx = new RegEx();
        this.transactionModel = new TransactionModel();
    }
    public String makeTransfer(String fromAccount, String toAccount, String amount, int userId){

        if(regEx.RegExNumbersLong(Long.parseLong(fromAccount)) &&
            regEx.RegExNumbersLong(Long.parseLong(toAccount)) &&
            regEx.RegExNumbersDouble(Double.parseDouble(amount))){

            long parsedFromAccount = Long.parseLong(fromAccount);
            long parsedToAccount = Long.parseLong(toAccount);
            double parsedAmount = Double.parseDouble(amount);

            if (transactionModel.makeTransfer(parsedFromAccount, parsedToAccount, parsedAmount, userId)) {
                return "Transfer successful";
            }
            return "Wrong account or amount";
        }
    return "Wrong input use numbers only";
    }
}
