package com.finsync;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

import static com.finsync.Transaction.TransactionType.BUY;
import static com.finsync.Transaction.TransactionType.SELL;
import static java.time.temporal.ChronoUnit.DAYS;

public class Account {
    private final static Logger LOG = LoggerFactory.getLogger(Account.class);

    private static int DIVIDEND_HISTORY_LIMIT = 5 * 365 + 20;

    private String accountId;
    private Brokerage brokerage;
    LocalDate lastRefreshed;
    double portfolioCostBasis;
    double portfolioValue;
    JsonObject holdingsRoot = null;
    JsonObject historyRoot = null;
    LocalDate lastTransactionDate;
    double cashValue = 0;
    Map<String, Holding> portfolio = new HashMap<>();

    //dividend profile related
    double totalAnnualDivAmount = 0;
    double[] monthlyDivAmount = new double[12]; //This is an approximation, should be very close except for few outliers
	double[] monthlyDivAmountNext = new double[12]; //This is an approximation, should be very close except for few outliers
	List<List<Holding>> monthlyDivHoldings = new ArrayList<List<Holding>>(12);


    public Account(String accountId) {
        this.accountId = accountId;
		this.lastRefreshed = LocalDate.MIN;
        for (int i = 0; i < 12; i++) {
            monthlyDivAmount[i] = 0;
	        monthlyDivAmountNext[i] = 0;
        }
        for (int i = 0; i < 12; i ++) {
            monthlyDivHoldings.add(new ArrayList<>());
        }
    }

    public void setBrokerage(Brokerage brokerage) {
        this.brokerage = brokerage;
    }


    public double getPortfolioCostBasis() {
        return this.portfolioCostBasis;
    }

    public double getCashValue() {
        return this.cashValue;
    }

    public double getPortfolioValue() {
        return this.portfolioValue;
    }

    public double getTotalAnnualDivAmount() {
        return this.totalAnnualDivAmount;
    }

    public Map<String, Holding> getPortfolio() {
        return this.portfolio;
    }

    public double[] getMonthlyDivAmount() {
        return this.monthlyDivAmount;
    }

	public double[] getMonthlyDivAmountNext() {
		return this.monthlyDivAmountNext;
	}

    public List<List<Holding>> getMonthlyDivHoldings() {
        return this.monthlyDivHoldings;
    }

    public void setLastTransactionDate(LocalDate date) {
        this.lastTransactionDate = date;
    }

    public void refresh() {
        try {
	        if (!(LocalDate.now().isAfter(this.lastRefreshed))) {
		        return;
	        }
	        this.cleanup();
            this.processHoldings();
            this.processTransactionHistory();
            this.updateHoldingsFromTransaction();
            this.updateDividendView();
            this.updateFromDb();
            this.lastRefreshed = LocalDate.now();
        } catch (Exception e) {
            LOG.error("exception:", e);
        }
    }

    private void cleanup() {
        portfolio.values().forEach((holding) -> {
            holding.reset();
        });
    }

    private void upComingDividend() {
        //just a view of holding.next* if greater thancurrent date
    }

    private void processHoldings() {
        boolean completed = false;
        if (this.holdingsRoot == null) {
            try {
                FileWriter fileWriter = new FileWriter("/tmp/finsync/holdings", false);
                String jsonHoldings = AllyApi.rest_execute(AllyApi.buildAccountUrl(this.accountId, AllyApi.ACCOUNT_URL), fileWriter);
                this.holdingsRoot = new JsonParser().parse(jsonHoldings).getAsJsonObject();
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                LOG.error("Exception: ", e);
            }
        }

        this.portfolioValue = 0;

        this.cashValue = this.holdingsRoot.get("response").getAsJsonObject()
                .get("accountbalance").getAsJsonObject()
                .get("money").getAsJsonObject()
                .get("cash").getAsDouble();

        this.portfolioValue += this.cashValue;
        JsonArray holdings = this.holdingsRoot.get("response").getAsJsonObject()
                .get("accountholdings").getAsJsonObject()
                .get("holding").getAsJsonArray();

        int hc = 0;

        for (JsonElement entry: holdings) {
            try {
                JsonObject holdingRoot = entry.getAsJsonObject();
                JsonObject displayData = holdingRoot.getAsJsonObject("displaydata");
                String ticker = displayData.get("symbol").getAsString();

                LOG.debug(++hc + ". Holding: " +  ticker);
                Holding holding = portfolio.get(ticker);
                if (holding == null) {
                    portfolio.put(ticker, new Holding(ticker));
                    holding = portfolio.get(ticker);
                }

                //fetch the ticker specific info
                JsonObject quoteRoot = null;
                if (holding.getQuoteRoot() == null) {
                    String quoteUrl = AllyApi.buildQuoteUrl(ticker);
                    FileWriter fileWriter = new FileWriter("/tmp/finsync/quote_" + ticker, false);
                    String quoteDetailsStr = AllyApi.rest_execute(quoteUrl, fileWriter);
                    fileWriter.flush();
                    fileWriter.close();
                    if (quoteDetailsStr == null || quoteDetailsStr.isEmpty()) {
                        continue;
                    }

                    JsonObject quotesRoot = new JsonParser().parse(quoteDetailsStr).getAsJsonObject();
                    quoteRoot = quotesRoot.get("response").getAsJsonObject()
                            .get("quotes").getAsJsonObject()
                            .get("quote").getAsJsonObject();


                    holding.setQuoteRoot(quoteRoot);
                    holding.setQuoteLastUpdated(System.currentTimeMillis());
                } else {
                    quoteRoot = holding.getQuoteRoot();
                }

                holding.setShares(StockUtil.getDoubleFromJson(holdingRoot.get("qty"), 0));
                holding.setPrice(StockUtil.getDoubleFromJson(holdingRoot.get("price"), 0));
                holding.setAvgPurchasePrice(StockUtil.getDoubleFromJson(holdingRoot.get("purchaseprice"), 0));
                holding.setCostBasis(StockUtil.getDoubleFromJson(holdingRoot.get("costbasis"), 0));
                holding.setValue(StockUtil.getDoubleFromJson(holdingRoot.get("marketvalue"), 0));
                holding.setValueChange(StockUtil.getDoubleFromJson(holdingRoot.get("marketvaluechange"), 0));
                holding.setPercentChange(StockUtil.getDoubleFromJson(quoteRoot.get("pchg"), 0));


                holding.setCurrentYield(StockUtil.getDoubleFromJson(quoteRoot.get("yield"), 0));
                holding.setNextDivAmountPerShare(StockUtil.getDoubleFromJson(quoteRoot.get("div"), 0));
                holding.setNextDivAmountTotal(holding.getShares() * holding.getNextDivAmountPerShare());
                DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                try {
                    holding.setNextDivDatePayment(LocalDate.parse(quoteRoot.get("divpaydt").getAsString(), dtFormatter));
                } catch (Exception e) {

                }

                String divFreq = quoteRoot.get("divfreq").getAsString();
                Holding.DivFrequency divFrequency = Holding.DivFrequency.NONE;
                double annualDivAmountPerShare = 0;
                double annualDivAmount = 0;
                if (divFreq.equalsIgnoreCase("A")) {
                    annualDivAmountPerShare = 1 * holding.getNextDivAmountPerShare();
                    divFrequency = Holding.DivFrequency.ANNUAL;
                } else if (divFreq.equalsIgnoreCase("S")) {
                    annualDivAmountPerShare = 2 * holding.getNextDivAmountPerShare();
                    divFrequency = Holding.DivFrequency.SEMIANUAL;
                } else if (divFreq.equalsIgnoreCase("Q")) {
                    annualDivAmountPerShare = 4 * holding.getNextDivAmountPerShare();
                    divFrequency = Holding.DivFrequency.QUARTELY;
                } else if (divFreq.equalsIgnoreCase("M")) {
                    annualDivAmountPerShare = 12 * holding.getNextDivAmountPerShare();
                    divFrequency = Holding.DivFrequency.MONTHLY;
                }
                annualDivAmount = annualDivAmountPerShare * holding.getShares();
                holding.setAnnualDivAmount(annualDivAmount);

                double annualEps = StockUtil.getDoubleFromJson(quoteRoot.get("eps"), 0);
                if (annualEps > 0 ) {
                    holding.setDivPayoutRatio((annualDivAmountPerShare * 100) / annualEps);
                }
                holding.setDivFrequency(divFrequency);
                holding.setYieldOnCostBasis((annualDivAmount * 100 ) / holding.getCostBasis());


                holding.setGailossOnCostBasis(StockUtil.getDoubleFromJson(holdingRoot.get("gainloss"), 0));
                holding.setTotalReturnOnCostBasis((holding.getGainlossOnCostBasis() * 100) / holding.getCostBasis());

                this.portfolioValue += holding.getValue();

                this.processDividendHistory(holding);
            } catch (Exception e) {
                LOG.error("exception ", e);
            }
        }
    }

    private void processTransactionHistory() {
        if (this.historyRoot == null) {
            try {
                FileWriter fileWriter = new FileWriter("/tmp/finsync/history", false);
                String jsonHistory = AllyApi.rest_execute(AllyApi.buildAccountUrl(this.accountId, AllyApi.HISTORY_URL), fileWriter);
                this.historyRoot = new JsonParser().parse(jsonHistory).getAsJsonObject();
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                LOG.error("Exception: ", e);
            }
        }

        JsonArray histories = this.historyRoot.get("response").getAsJsonObject()
                .get("transactions").getAsJsonObject()
                .get("transaction").getAsJsonArray();

        this.portfolioCostBasis = 0;

        for (int i = histories.size() - 1; i >=0; i--) {
            JsonElement entry = histories.get(i);
            try {
                JsonObject transactionRoot = entry.getAsJsonObject();
                JsonObject transactionIntRoot = transactionRoot.get("transaction").getAsJsonObject();

                LocalDate transDate = LocalDateTime.parse(transactionRoot.get("date").getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();

                if (transDate.isBefore(this.lastTransactionDate)) {
                    continue;
                }

                String ticker = transactionRoot.get("symbol").getAsString();
                double amount = StockUtil.getDoubleFromJson(transactionRoot.get("amount"), 0);
                String activity = transactionRoot.get("activity").getAsString();

                Transaction.TransactionType type = Transaction.TransactionType.NONE;
                if (activity.equalsIgnoreCase("Dividend")) {
                    type = Transaction.TransactionType.DIVIDEND;
                } else if (activity.equalsIgnoreCase("Bookkeeping")) {
                    String desc = transactionRoot.get("desc").getAsString();
                    if (!desc.contains("ACH")) {
                        type = Transaction.TransactionType.REINVESTMENT;
                        amount = amount * (-1);
                    } else if (desc.contains("ACH WITHDRAWAL")) {
                        this.portfolioCostBasis -= amount;
                    } else if (desc.contains("ACH DEPOSIT")) {
                        this.portfolioCostBasis += amount;
                    }
                } else if (activity.equalsIgnoreCase("Reinvestment")) {
                    //skip
                } else if (activity.equalsIgnoreCase("Trade")) {
                    if (amount < 0) {
                        type = Transaction.TransactionType.BUY;
                        amount = amount * (-1);
                    } else {
                        type = SELL;
                    }
                }

                if (type == Transaction.TransactionType.NONE) {
                    continue;
                }

                Transaction transaction = new Transaction(ticker);
                transaction.setType(type);
                transaction.setDate(LocalDateTime.parse(transactionRoot.get("date").getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate());
                transaction.setAmount(amount);
                transaction.setShares(StockUtil.getDoubleFromJson(transactionIntRoot.get("quantity"), 0));
                transaction.setPrice(StockUtil.getDoubleFromJson(transactionIntRoot.get("price"), 0));
                if (transaction.getShares() > 0 && transaction.getPrice() == 0) {
                    transaction.setPrice(transaction.getAmount() / transaction.getShares());
                }

                Holding holding = this.portfolio.get(ticker);
                if (holding != null) {
                    holding.addTransaction(transaction);
                    if (transaction.getType() == BUY) {
                        holding.addBuyTransaction(transaction);
                    }
                }
            } catch (Exception e) {
                LOG.error("exception ", e);
            }
        }
    }

    private void updateHoldingsFromTransaction() {
        portfolio.values().forEach((holding) -> {
            try {
                boolean latestDiv = false;
                boolean adjustCostBasis = false;
                holding.setOriginalCostBasis(0);
                holding.setTotalDivReceived(0);
                List<Transaction> transactions = holding.getTransactions();
                List<Transaction> buyTransactions = holding.getBuyTransactions();

                holding.setFirstBuyDate(transactions.get(transactions.size() - 1).getDate());
                holding.pruneAndReverseDividendTransactions();
                for (Transaction transaction : transactions) {
                    try {
                        switch (transaction.getType()) {
                            case DIVIDEND:
                                holding.setTotalDivReceived(holding.getTotalDivReceived() + transaction.getAmount());
                                if (!latestDiv) {
                                    holding.setLastDivAmountTotal(transaction.getAmount());
                                    holding.setLastDivDatePayment(transaction.getDate());
                                    //holding.setLastDivAmountPerShare();
                                    latestDiv = true;
                                }
                                holding.matchDividendTransaction(transaction);
                                break;
                            case REINVESTMENT:
                                break;
                            case BUY:
                                holding.setOriginalCostBasis(holding.getOriginalCostBasis() + transaction.getAmount());
                                break;
                            case SELL:
                                adjustCostBasis = true;
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        LOG.error("exception ", e);
                    }
                }

                if (adjustCostBasis) {
                    LOG.debug("Sell: ticker " + holding.getTicker());
                    //FIFO based cost basis accounting
                    for (int i = transactions.size() - 1; i >= 0; i--) {
                        if (transactions.get(i).getType() == SELL) {
                            Transaction sell = transactions.get(i);
                            double sellshares = sell.getShares() * -1;
                            for (int j = buyTransactions.size() - 1; j >= 0; j--) {
                                Transaction buy = buyTransactions.get(j);
                                if (buy.getShares() > 0) {
                                    double matchshares = buy.getShares() > sellshares ? sellshares : buy.getShares();
                                    holding.setOriginalCostBasis(holding.getOriginalCostBasis() - (matchshares * buy.getPrice()));
                                    buy.setShares(buy.getShares()-matchshares);
                                    sellshares -= matchshares;
                                }
                                if (sellshares == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }

                holding.setYieldOnOriginalCostBasis((holding.getAnnualDivAmount() * 100) / holding.getOriginalCostBasis());
                holding.setGailossOnOriginalCostBasis(holding.getValue() - holding.getOriginalCostBasis());
                holding.setTotalReturnOnOriginalCostBasis((holding.getGainlossOnOriginalCostBasis() * 100) / holding.getOriginalCostBasis());
            } catch (Exception e) {
                LOG.error("exception ", e);
            }
        });
    }

    //TBD
    //lastDivAmountPerShare

    private void processDividendHistory(Holding holding) {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String redflag;
        String url = "http://www.nasdaq.com/symbol/" + holding.getTicker() + "/dividend-history";
        Document doc = null;

        DGRProfile dgrProfile = holding.getDivDGRProfile();
        if (dgrProfile == null) {
            dgrProfile = new DGRProfile(holding.getTicker());
            holding.setDivDGRProfile(dgrProfile);
        }
        dgrProfile.resetProfile();

        doc = dgrProfile.getDocument();
        if (doc == null) {
            try {
                HttpRequestFactory requestFactory
                        = new NetHttpTransport().createRequestFactory();
                HttpRequest request = requestFactory.buildGetRequest(
                        new GenericUrl(url));
                String rawResponse = request.execute().parseAsString();
                //doc = Jsoup.connect(url).get();
                doc = Jsoup.parse(rawResponse);
                dgrProfile.setDocument(doc);
            } catch (Exception e) {
                return;
            }
        }

        Element body = doc.body();
        Element divRoot = body.getElementsByClass("genTable").get(0);
        if (divRoot == null) {
            return;
        }
        Elements divRows;
        try {
            divRows = divRoot.getElementById("quotes_content_left_dividendhistoryGrid").select("tbody").get(0).select("tr");
        } catch (Exception e) {
            return;
        }

        int rowCount = divRows.size();
        LocalDate currentDT = LocalDate.now();
        LocalDate lastDT = currentDT.minusDays(DIVIDEND_HISTORY_LIMIT);
        LocalDate lastDividendMatchingDT = currentDT.minusDays(1 * 365  + 20);

        double prevAmount = 0.0;
        LocalDate prevExDate = null;
        int skip = 0;

        for (int i = rowCount - 1; i >= 0; i--) {
            Element divRow = divRows.get(i);
            Elements divCols = divRow.select("td");
            LocalDate exDate = LocalDate.parse(divCols.get(0).text(), dtFormatter);

            if (exDate.isBefore(lastDT)) {
                continue;
            }

            boolean split = false;
            LocalDate payDate;
            double amount;
            try {
                payDate = LocalDate.parse(divCols.get(5).text(), dtFormatter);
                amount = Double.valueOf(divCols.get(2).text());
            } catch (Exception e) {
                continue;
            }

            if (payDate.isAfter(lastDividendMatchingDT) && payDate.isBefore(LocalDate.now())) {
                DividendTransaction dividendTransaction = new DividendTransaction(holding.getTicker());
                dividendTransaction.setExpectedDate(payDate);
                dividendTransaction.setExpectedAmount(amount * holding.getShares());
                holding.addDivTransaction(dividendTransaction);
            }

            if (prevAmount == 0.0) {
                prevAmount = amount;
                prevExDate = exDate;
            }

            //ignore the special ones
            if (holding.getDivFrequency() == Holding.DivFrequency.QUARTELY &&
                    DAYS.between(prevExDate, exDate) < 80) {
                continue;
            }

            if (prevAmount != amount || i == 0) {
                DGRProfile.DGREvent dgrEvent = new DGRProfile.DGREvent();
                DGRProfile.DGRRating rating = DGRProfile.DGRRating.NONE;

                //adjust for stock split if any
                if (amount < prevAmount) {
                    List<StockSplitHistory.StockSplitEvent> splitHistories = StockSplitHistory.getStockSplitEvents(holding.getTicker());
                    if (splitHistories != null) {
                        LocalDate lDate = exDate.minusDays(3 * 30);
                        for (StockSplitHistory.StockSplitEvent event: splitHistories) {
                            if (event.getDate().isAfter(lDate)) {
                                split = true;
                                prevAmount = prevAmount * event.getRatio();
                                break;
                            }
                        }
                    }
                }

                double adiff = amount - prevAmount;
                double achange = (adiff * 100) / prevAmount;
                Period period = Period.between(exDate, prevExDate);
                long daysBetween = DAYS.between(prevExDate, exDate);

                //if (((period.getDays() / 385) > 0) || ((i == 0) && (skip > 3))) {
                if (((daysBetween/440) > 0) || ((i == 0) && (skip > 3))) {
                    rating = DGRProfile.DGRRating.STALLED;
                } else if (achange <= 4.0) {
                    if (achange < 0.0) {
                        rating = DGRProfile.DGRRating.CUT;
                    } else if (achange > 0.0) {
                        rating = DGRProfile.DGRRating.LOW;
                    }
                } else if (achange > 4.0 && achange < 8.0){
                    rating = DGRProfile.DGRRating.MEDIUM;
                } else{
                    rating = DGRProfile.DGRRating.HIGH;
                }

                dgrEvent.setDivExDate(exDate)
                        .setDivPayDate(payDate)
                        .setDivAmount(amount)
                        .setDivPrevAmount(prevAmount)
                        .setGrowthPercentage(achange)
                        .setDaysBetween(daysBetween)
                        .setSplit(split)
                        .setRating(rating);

                dgrProfile.addDGREvent(dgrEvent);

                prevAmount = amount;
                prevExDate = exDate;
                skip = 0;
            } else {
                skip = skip + 1;
                continue;
            }
        }

        dgrProfile.reverseDgrHistory();
        dgrProfile.updateProfiles();
        return;
    }

    private void updateDividendView() {
        int h1 = 0, h2 = 6;
        int q1 = 0, q2 = 3, q3 = 6, q4 = 9;

        //reset
        this.totalAnnualDivAmount = 0;
        for (int i = 0; i < 12; i++) {
            monthlyDivAmount[i] = 0;
	        monthlyDivAmountNext[i] = 0;
        }

        monthlyDivHoldings.clear();
        for (int i = 0; i < 12; i ++) {
            monthlyDivHoldings.add(new ArrayList<>());
        }

        for (Holding holding : portfolio.values()) {
            try {
                if (holding.getAnnualDivAmount() == 0) {
                    continue;
                }
                this.totalAnnualDivAmount += holding.getAnnualDivAmount();

                int month = holding.getLastDivDatePayment().getMonthValue() - 1;
                double amount = holding.getLastDivAmountTotal();
	            double amountNext = holding.getNextDivAmountTotal();
	            if (holding.getDivFrequency() == Holding.DivFrequency.ANNUAL) {
                    monthlyDivAmount[month] += amount;
	                monthlyDivAmountNext[month] += amountNext;

                    monthlyDivHoldings.get(month).add(holding);
                } else if (holding.getDivFrequency() == Holding.DivFrequency.SEMIANUAL) {
                    monthlyDivAmount[(month % 6) + h1] += amount;
                    monthlyDivAmount[(month % 6) + h2] += amount;
		            monthlyDivAmountNext[(month % 6) + h1] += amountNext;
		            monthlyDivAmountNext[(month % 6) + h2] += amountNext;

                    monthlyDivHoldings.get((month % 6) + h1).add(holding);
                    monthlyDivHoldings.get((month % 6) + h2).add(holding);
                } else if (holding.getDivFrequency() == Holding.DivFrequency.QUARTELY) {
                    monthlyDivAmount[(month % 3) + q1] += amount;
                    monthlyDivAmount[(month % 3) + q2] += amount;
                    monthlyDivAmount[(month % 3) + q3] += amount;
                    monthlyDivAmount[(month % 3) + q4] += amount;
		            monthlyDivAmountNext[(month % 3) + q1] += amountNext;
		            monthlyDivAmountNext[(month % 3) + q2] += amountNext;
		            monthlyDivAmountNext[(month % 3) + q3] += amountNext;
		            monthlyDivAmountNext[(month % 3) + q4] += amountNext;

                    monthlyDivHoldings.get((month % 3) + q1).add(holding);
                    monthlyDivHoldings.get((month % 3) + q2).add(holding);
                    monthlyDivHoldings.get((month % 3) + q3).add(holding);
                    monthlyDivHoldings.get((month % 3) + q4).add(holding);
                } else if (holding.getDivFrequency() == Holding.DivFrequency.MONTHLY) {
                    for (int i = 0; i < 12; i++) {
                        monthlyDivAmount[i] += amount;
	                    monthlyDivAmountNext[i] += amountNext;
	                    monthlyDivHoldings.get(i).add(holding);
                    }
                }
            } catch (Exception e) {
                LOG.error("exception ", e);
            }
        }
    }

    private void updateFromDb() {
        String dbFile = "db/" + this.brokerage.brokerage + "_" + this.accountId + "_holdings.csv";

        CSVReader reader = null;
        try {
            String file = Thread.currentThread().getContextClassLoader().getResource(dbFile).getFile();
            reader = new CSVReader(new FileReader(file));
            String[] line;
            while ((line = reader.readNext()) != null) {
                Holding holding = this.portfolio.get(line[0]);
                if (holding != null) {
                    holding.setReinvestment(line[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
