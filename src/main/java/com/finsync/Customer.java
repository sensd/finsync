package com.finsync;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Customer {
	String customerId;
	Map<String, Brokerage> brokerages = new HashMap<>();

	public Customer(String customerId) {
		this.customerId = customerId;
	}

	public Map<String, Brokerage> getBrokerages() {
		return this.brokerages;
	}

	public Brokerage getBrokerage(String brokerageName) {
		return this.brokerages.get(brokerageName);
	}

	private Brokerage addBrokerage(String brokerageName) {
		Brokerage brokerage = brokerages.get(brokerageName);
		if (brokerage == null) {
			brokerage = new Brokerage(brokerageName);
			brokerages.put(brokerageName, brokerage);
		}

		return brokerage;
	}

	public void refresh(String brokerageName, String accountId, String firstTransactionDate) {
		Brokerage brokerage = this.addBrokerage(brokerageName);
		brokerage.refresh(accountId, firstTransactionDate);
	}

	public void refresh(String brokerageName) {
		Brokerage brokerage = this.addBrokerage(brokerageName);
		brokerage.refresh();
	}

	public Brokerage getFirstBrokerage() {
		return brokerages.values().iterator().next();
	}
}
