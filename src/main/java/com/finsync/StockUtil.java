package com.finsync;

import com.finsync.jersey.WebServerHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StockUtil {
	private final static Logger LOG = LoggerFactory.getLogger(WebServerHandler.class);
	public static double getDoubleFromJson(JsonElement obj, double defValue) {
		double value = defValue;
		try {
			if (obj.getAsString().isEmpty() || obj.getAsString().equalsIgnoreCase("na")) {
				return value;
			}
			value = obj.getAsDouble();
		} catch (Exception e){
			LOG.error("exception parsing double ", e);
		}
		return value;
	}

	static Set<String> foreign = new HashSet<String>();
	static Set<String> funds = new HashSet<String>();

	static {
		foreign.addAll(Arrays.asList("BTI", "ENB", "TD", "TRP", "UL"));
		funds.addAll(Arrays.asList("GDX", "HTD", "HYD", "IAU", "MAIN", "PFF", "SCHD", "SLYG", "STK", "UTG", "XLF"));
	}

	public static boolean isForeignOrFunds(String ticker) {
		return (foreign.contains(ticker) | funds.contains(ticker));
	}
}
