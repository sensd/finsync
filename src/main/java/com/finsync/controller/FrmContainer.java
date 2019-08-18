package com.finsync.controller;

import com.finsync.*;
import com.finsync.frm.*;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FrmContainer {
	private final static Logger LOG = LoggerFactory.getLogger(FrmContainer.class);

	static Configuration cfg;

	static {
		/* Create and adjust the configuration singleton */
		cfg = new Configuration(Configuration.VERSION_2_3_28);
		try {
			cfg.setDirectoryForTemplateLoading(new File(FrmContainer.class.getClassLoader().getResource(".").getFile()));
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setLogTemplateExceptions(false);
			cfg.setWrapUncheckedExceptions(true);
		} catch (Exception e) {
			LOG.error("Exception: ", e);
		}
	}


	public static String processFrmex () {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();
		root.put("user", "Big Joe");
		Product latest = new Product();
		latest.setUrl("products/greenmouse.html");
		latest.setName("green mouse");
		root.put("latestProduct", latest);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("frmex.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processFrmex1 () {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();


		Map<String, Product> products = new HashMap();
		Product pa = new Product("/a", "producta");
		Product pb = new Product("/b", "productb");
		products.put("a", pa);
		products.put("b", pb);

		root.put("products", products);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("frmex1.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processHoldings () {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		Map<String, Holding> holdings = account.getPortfolio();
		root.put("account", account);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("holdings.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processMonthly () {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		root.put("account", account);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("monthly_grid.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processDividends () {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		Map<String, Holding> holdings = account.getPortfolio();
		List<Holding> upcoming = new ArrayList<>();
		List<DividendTransaction> past = new ArrayList<>();
		LocalDate past3m = LocalDate.now().minusDays(91);
		LocalDate yesterday = LocalDate.now().minusDays(1);

		Map<String, List<Double>> dgrBins = new HashMap<>();

		//add the bins
		for (DGRProfile.DGRRating rating : DGRProfile.DGRRating.values()) {
			List<Double> bins = new ArrayList<>(3);
			bins.add(0.0);
			bins.add(0.0);
			bins.add(0.0);
			dgrBins.put(rating.toString(), bins);
		}

		for (Holding holding : holdings.values()) {
			//find the upcoming ones
			if (holding.getNextDivDatePayment() != null && holding.getNextDivDatePayment().isAfter(yesterday)) {
				upcoming.add(holding);
			}

			//find the past ones
			for (DividendTransaction dividendTransaction: holding.getDivTransactions()) {
				if (dividendTransaction.getExpectedDate().isBefore(LocalDate.now()) &&
						dividendTransaction.getExpectedDate().isAfter(past3m)) {
					past.add(dividendTransaction);
				}
			}

			List<Double> dgrBin = dgrBins.get(holding.getDivDGRProfile().getRatingLast().toString());
			dgrBin.set(0, dgrBin.get(0) + holding.getOriginalCostBasis());
			dgrBin.set(1, dgrBin.get(1) + holding.getValue());
			dgrBin.set(2, dgrBin.get(2) + 1);

		}
		root.put("dgrbins", dgrBins);

		Collections.sort(upcoming, new Comparator<Holding>() {
			@Override
			public int compare(Holding o1, Holding o2) {
				if (o1.getNextDivDatePayment().isBefore(o2.getNextDivDatePayment())) {
					return -1;
				} else if (o1.getNextDivDatePayment().isAfter(o2.getNextDivDatePayment())) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		root.put("upcoming", upcoming);


		Collections.sort(past, new Comparator<DividendTransaction>() {
			@Override
			public int compare(DividendTransaction o1, DividendTransaction o2) {
				if (o1.getExpectedDate().isBefore(o2.getExpectedDate())) {
					return -1;
				} else if (o1.getExpectedDate().isAfter(o2.getExpectedDate())) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		root.put("past", past);


		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("dividends.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processTransactions (String ticker) {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		Map<String, Holding> holdings = account.getPortfolio();
		List<Transaction> transactions = holdings.get(ticker).getTransactions();
		root.put("transactions", transactions);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("transactions.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processDivTransactions (String ticker) {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		Map<String, Holding> holdings = account.getPortfolio();
		List<DividendTransaction> divTransactions = holdings.get(ticker).getDivTransactions();
		root.put("divtransactions", divTransactions);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("divtransactions.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}

	public static String processDGRHistory (String ticker) {
		/* Create a data-model */
		String response = null;
		Map<String, Object> root = new HashMap();

		Account account = FinSync.customer.getFirstBrokerage().getFirstAccount();
		Map<String, Holding> holdings = account.getPortfolio();
		DGRProfile dgrProfile = holdings.get(ticker).getDivDGRProfile();
		root.put("dgrprofile", dgrProfile);

		try {
			/* Get the template (uses cache internally) */
			Template temp = cfg.getTemplate("dgrhistory.ftl");

			/* Merge data-model with template */
			//Writer out = new OutputStreamWriter(System.out);
			StringWriter out = new StringWriter(2048);
			temp.process(root, out);
			out.flush();
			response = out.toString();
			// Note: Depending on what `out` is, you may need to call `out.close()`.
			// This is usually the case for file output, but not for servlet output.
		} catch (Exception e) {
			LOG.error("exception:", e);
		}
		return response;
	}
}
