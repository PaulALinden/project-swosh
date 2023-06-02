package org.example.model;

import java.time.LocalDateTime;
@SuppressWarnings("unused")
public class TransactionModel {
    private long senderAccountNumber;
    private long receiverAccountNumber;
    private double transactionValue;
    private LocalDateTime time;

    public TransactionModel() {
    }

    public long getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(long senderId) {
        this.senderAccountNumber = senderId;
    }

    public long getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(long receiverId) {
        this.receiverAccountNumber = receiverId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(double transactionValue) {
        this.transactionValue = transactionValue;
    }

}
