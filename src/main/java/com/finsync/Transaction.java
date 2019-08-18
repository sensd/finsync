package com.finsync;

import java.time.LocalDate;

public class Transaction {
    private String ticker = "";
    private TransactionType type = TransactionType.NONE;
    private LocalDate date;
    private double price = 0;
    private double shares = 0;
    private double amount = 0;
    //double yield = 0;

    public Transaction(String ticker) {
        this.ticker = ticker;
    }

    public String getTicker() {
        return this.ticker;
    }

    public Transaction setType(TransactionType type) {
        this.type = type;
        return this;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getPrice () {
        return price;
    }

    public Transaction setShares (double shares) {
        this.shares = shares;
        return this;
    }

    public double getShares () {
        return shares;
    }

    public Transaction setDate (LocalDate date ) {
        this.date = date;
        return this;
    }

    public LocalDate getDate () {
        return date;
    }

    /*
    public Transaction setYield (double yield) {
        this.yield = yield;
        return this;
    }

    public double getYield () {
        return yield;
    }
    */

    public Transaction setAmount (double amount) {
        this.amount = amount;
        return this;
    }

    public double getAmount () {
        return amount;
    }

    public enum TransactionType {
        NONE, BUY, SELL, DIVIDEND, REINVESTMENT
    }

}
