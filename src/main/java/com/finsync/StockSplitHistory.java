package com.finsync;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockSplitHistory {

	String ticker;
	public static Map<String, List<StockSplitEvent>> stockSplitsHistory = new HashMap<>();

	//this is statically configured.
	static {
		//AAPL
		addStockSplitEvent("AAPL", new StockSplitEvent(LocalDate.of(2014, 6, 9), 1.0/7));
		//AOS
		addStockSplitEvent("AOS", new StockSplitEvent(LocalDate.of(2016, 10, 06), 1.0/2));
		//CMCSA
		addStockSplitEvent("CMCSA", new StockSplitEvent(LocalDate.of(2017, 02, 21), 1.0/2));
		//HRL
		addStockSplitEvent("HRL", new StockSplitEvent(LocalDate.of(2016, 2, 16), 1.0/2));
		//V
		addStockSplitEvent("V", new StockSplitEvent(LocalDate.of(2015, 03, 19), 1.0/4));


	}


	public static void addStockSplitEvent(String ticker, StockSplitEvent event) {
		if (stockSplitsHistory.get(ticker) == null) {
			stockSplitsHistory.put(ticker, new ArrayList<>());
		}
		stockSplitsHistory.get(ticker).add(event);
	}

	public static List<StockSplitEvent> getStockSplitEvents(String ticker) {
		return stockSplitsHistory.get(ticker);
	}

	public static class StockSplitEvent {
		LocalDate date;
		double ratio;

		public StockSplitEvent(LocalDate date, double ratio) {
			this.date = date;
			this.ratio = ratio;
		}

		public LocalDate getDate() {
			return this.date;
		}

		public double getRatio() {
			return ratio;
		}
	}
}
