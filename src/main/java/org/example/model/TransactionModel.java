package org.example.model;

import java.time.LocalDateTime;

public class TransactionModel {

    private int senderId;
    private int saverId;

    private long transactionValue;

    private LocalDateTime time;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getSaverId() {
        return saverId;
    }

    public void setSaverId(int saverId) {
        this.saverId = saverId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public long getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(long transactionValue) {
        this.transactionValue = transactionValue;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
}
