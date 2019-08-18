package com.finsync;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Brokerage {
	String brokerage;
	Map<String, Account> accounts = new HashMap<>();

	public Brokerage(String brokerage) {
		this.brokerage = brokerage;
	}

	public Map<String, Account> getAccounts() {
		return this.accounts;
	}

	public Account getAccount(String accountId) {
		return this.accounts.get(accountId);
	}

	private Account addAccount(String accountId) {
		Account account = accounts.get(accountId);

		if (account == null) {
			account = new Account(accountId);
			account.setBrokerage(this);
			accounts.put(accountId, account);
		}

		return account;
	}

	public void refresh(String accountId, String firstTransactionDate) {
		Account account = this.addAccount(accountId);
		account.setLastTransactionDate(LocalDate.parse(firstTransactionDate));
		account.refresh();
	}


	public void refresh() {
		for (Account account : accounts.values()) {
			account.refresh();
		}
	}

	public Account getFirstAccount() {
		return accounts.values().iterator().next();
	}
}
