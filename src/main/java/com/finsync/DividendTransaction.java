package com.finsync;

import java.time.LocalDate;

public class DividendTransaction {
	private String ticker = "";
	private LocalDate expectedDate;
	private double expectedAmount = 0;
	private Transaction matchingTransaction = null;

	public DividendTransaction(String ticker) {
		this.ticker = ticker;
	}

	public String getTicker() {
		return this.ticker;
	}

	public DividendTransaction setExpectedDate(LocalDate date) {
		this.expectedDate = date;
		return this;
	}

	public LocalDate getExpectedDate() {
		return this.expectedDate;
	}

	public DividendTransaction setExpectedAmount(double amount) {
		this.expectedAmount = amount;
		return this;
	}

	public double getExpectedAmount() {
		return this.expectedAmount;
	}

	public DividendTransaction setMatchingTransaction(Transaction transaction) {
		this.matchingTransaction = transaction;
		return this;
	}

	public Transaction getMatchingTransaction() {
		return this.matchingTransaction;
	}
}
