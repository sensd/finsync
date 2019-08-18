package com.finsync;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.finsync.controller.FrmContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FinSync {
	private final static Logger LOG = LoggerFactory.getLogger(FinSync.class);
	public static Customer customer;

	private static void test1() {
		LocalDate date;
		DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		try {
			date = LocalDate.parse("12/7/2018", dtFormatter);
		} catch (Exception e) {
			LOG.error("exception", e);
		}

	}
	public static void main(String[] argv) {
		//test1();
		//FrmContainer controller = new FrmContainer();

		Config conf = ConfigFactory.load();
		String customerName = conf.getString("finsync.customer");
		if (!conf.hasPath("finsync.brokerage.ally")) {
			System.exit(-1);
		}
		AllyApi.setUpAlly(conf.getString("finsync.brokerage.ally.consumer_key"),
							conf.getString("finsync.brokerage.ally.consumer_secret"),
							conf.getString("finsync.brokerage.ally.oauth_token"),
								conf.getString("finsync.brokerage.ally.oauth_token_secret"));
		customer = new Customer(customerName);
		customer.refresh("ally", conf.getString("finsync.brokerage.ally.account"),
				conf.getString("finsync.brokerage.ally.first_transaction_date"));

		new WebServer().run();;
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}
		}
	}
}
