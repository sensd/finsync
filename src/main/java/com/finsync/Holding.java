package com.finsync;

import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Holding {
    private String ticker = "";
    private double shares = 0; //total share count
    private double price = 0;
    private double avgPurchasePrice = 0; //avg price of all buy transaction
    private double originalCostBasis; //original money invested, excludes dividends.
    private double costBasis;  //total money invested
    private double value; //total current value
    private double valueChange; //change in value since last close;
    private double percentChange; //change in percent since last close;

    private double currentYield; //current yield of the ticker
    private double yieldOnCostBasis; //yield on costBasis
    private double yieldOnOriginalCostBasis; //yield on originalCostBasis
    private LocalDate nextDivDatePayment;
    private double nextDivAmountPerShare;
    private double nextDivAmountTotal;
    private LocalDate lastDivDatePayment; //last* fields are actual that was recorded in the account
    private double lastDivAmountPerShare;
    private double lastDivAmountTotal;
    private double annualDivAmount;
    private double divPayoutRatio;
    private DivFrequency divFrequency;
    private DGRProfile divDGRProfile;
    private String reinvestment;
    private double totalDivReceived; //total dividend received for entire holding period

    private double gainlossOnOriginalCostBasis;
    private double gainlossOnCostBasis;
    private double totalReturnOnOriginalCostBasis; //percentage terms
    private double totalReturnOnCostBasis; //percentage terms

    private LocalDate firstBuyDate = null;
    private List<Transaction> buyTransactions;
    private List<Transaction> transactions;
    private List<DividendTransaction> divTransactions;

    private JsonObject quoteRoot = null;
    private long quoteLastUpdated;

    public Holding(String ticker) {
        this.ticker = ticker;
        /*
        transactions = new TreeSet<>(new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction, Transaction t1) {
                return 0;
            }
        });
        */
        buyTransactions = new LinkedList<>();
        transactions = new LinkedList<>();
        divTransactions = new LinkedList<>();

    }

    public String getTicker() {
        return this.ticker;
    }

    public Holding setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Holding setShares(double shares) {
        this.shares = shares;
        return this;
    }

    public double getShares () {
        return shares;
    }

    public Holding setAvgPurchasePrice(double avgPurchasePrice) {
        this.avgPurchasePrice = avgPurchasePrice;
        return this;
    }

    public double getAvgPurchasePrice () {
        return avgPurchasePrice;
    }

    public Holding setOriginalCostBasis(double originalCostBasis) {
        this.originalCostBasis = originalCostBasis;
        return this;
    }

    public double getOriginalCostBasis () {
        return originalCostBasis;
    }

    public Holding setCostBasis(double costBasis) {
        this.costBasis = costBasis;
        return this;
    }

    public double getCostBasis () {
        return costBasis;
    }

    public Holding setValue(double value) {
        this.value = value;
        return this;
    }

    public double getValue () {
        return value;
    }

    public Holding setValueChange(double valueChange) {
        this.valueChange = valueChange;
        return this;
    }

    public double getValueChange () {
        return valueChange;
    }

    public Holding setPercentChange(double percentChange) {
        this.percentChange = percentChange;
        return this;
    }

    public double getPercentChange () {
        return percentChange;
    }

    public Holding setCurrentYield(double currentYield) {
        this.currentYield = currentYield;
        return this;
    }

    public double getCurrentYield() {
        return currentYield;
    }

    public Holding setYieldOnCostBasis(double yieldOnCostBasis) {
        this.yieldOnCostBasis = yieldOnCostBasis;
        return this;
    }

    public double getYieldOnCostBasis () {
        return yieldOnCostBasis;
    }

    public Holding setYieldOnOriginalCostBasis(double yieldOnOriginalCostBasis) {
        this.yieldOnOriginalCostBasis = yieldOnOriginalCostBasis;
        return this;
    }

    public double getYieldOnOriginalCostBasis () {
        return yieldOnOriginalCostBasis;
    }

    public Holding setNextDivDatePayment(LocalDate nextDivDatePayment) {
        this.nextDivDatePayment = nextDivDatePayment;
        return this;
    }

    public LocalDate getNextDivDatePayment () {
        return nextDivDatePayment;
    }

    public Holding setNextDivAmountPerShare(double nextDivAmountPerShare) {
        this.nextDivAmountPerShare = nextDivAmountPerShare;
        return this;
    }

    public double getNextDivAmountPerShare () {
        return nextDivAmountPerShare;
    }

    public Holding setNextDivAmountTotal(double nextDivAmountTotal) {
        this.nextDivAmountTotal = nextDivAmountTotal;
        return this;
    }

    public double getNextDivAmountTotal () {
        return nextDivAmountTotal;
    }

    public Holding setLastDivDatePayment(LocalDate lastDivDatePayment) {
        this.lastDivDatePayment = lastDivDatePayment;
        return this;
    }

    public LocalDate getLastDivDatePayment () {
        return lastDivDatePayment;
    }

    public Holding setLastDivAmountPerShare(double lastDivAmountPerShare) {
        this.lastDivAmountPerShare = lastDivAmountPerShare;
        return this;
    }

    public double getLastDivAmountPerShare () {
        return lastDivAmountPerShare;
    }

    public Holding setLastDivAmountTotal(double lastDivAmountTotal) {
        this.lastDivAmountTotal = lastDivAmountTotal;
        return this;
    }

    public double getLastDivAmountTotal () {
        return lastDivAmountTotal;
    }

    public Holding setAnnualDivAmount(double annualDivAmount) {
        this.annualDivAmount = annualDivAmount;
        return this;
    }

    public double getAnnualDivAmount () {
        return annualDivAmount;
    }

    public Holding setDivPayoutRatio(double divPayoutRatio) {
        this.divPayoutRatio = divPayoutRatio;
        return this;
    }

    public double getDivPayoutRatio () {
        return divPayoutRatio;
    }

    public Holding setDivFrequency (DivFrequency divFrequency) {
        this.divFrequency = divFrequency;
        return this;
    }

    public DivFrequency getDivFrequency () {
        return this.divFrequency;
    }

    public Holding setDivDGRProfile(DGRProfile divDGRProfile) {
        this.divDGRProfile = divDGRProfile;
        return this;
    }

    public DGRProfile getDivDGRProfile () {
        return divDGRProfile;
    }

    public Holding setReinvestment(String reinvestment) {
        this.reinvestment = reinvestment;
        return this;
    }

    public String getReinvestment () {
        return reinvestment;
    }

    public Holding setTotalDivReceived (double totalDivReceived) {
        this.totalDivReceived = totalDivReceived;
        return this;
    }

    public double getTotalDivReceived() {
        return this.totalDivReceived;
    }

    public Holding setGailossOnOriginalCostBasis(double gainlossOnOriginalCostBasis) {
        this.gainlossOnOriginalCostBasis = gainlossOnOriginalCostBasis;
        return this;
    }

    public double getGainlossOnOriginalCostBasis () {
        return gainlossOnOriginalCostBasis;
    }

    public Holding setGailossOnCostBasis(double gainlossOnCostBasis) {
        this.gainlossOnCostBasis = gainlossOnCostBasis;
        return this;
    }

    public double getGainlossOnCostBasis () {
        return gainlossOnCostBasis;
    }

    public Holding setTotalReturnOnOriginalCostBasis(double totalReturnOnOriginalCostBasis) {
        this.totalReturnOnOriginalCostBasis = totalReturnOnOriginalCostBasis;
        return this;
    }

    public double getTotalReturnOnOriginalCostBasis () {
        return totalReturnOnOriginalCostBasis;
    }

    public Holding setTotalReturnOnCostBasis(double totalReturnOnCostBasis) {
        this.totalReturnOnCostBasis = totalReturnOnCostBasis;
        return this;
    }

    public double getTotalReturnOnCostBasis () {
        return totalReturnOnCostBasis;
    }

    public Holding setQuoteRoot (JsonObject quoteRoot) {
        this.quoteRoot = quoteRoot;
        return this;
    }

    public JsonObject getQuoteRoot() {
        return this.quoteRoot;
    }

    public Holding setQuoteLastUpdated (long time) {
        this.quoteLastUpdated = time;
        return this;
    }

    public LocalDate getFirstBuyDate() {
        return this.firstBuyDate;
    }

    public Holding setFirstBuyDate(LocalDate firstBuyDate) {
        this.firstBuyDate = firstBuyDate;
        return this;
    }

    public Holding addBuyTransaction (Transaction transaction) {
        buyTransactions.add(transaction);
        return this;
    }

    public List<Transaction> getBuyTransactions () {
        return this.buyTransactions;
    }

    public Holding addTransaction (Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    public List<Transaction> getTransactions () {
        return this.transactions;
    }


    public Holding addDivTransaction (DividendTransaction transaction) {
        divTransactions.add(transaction);
        return this;
    }

    public List<DividendTransaction> getDivTransactions () {
        return this.divTransactions;
    }

    public void pruneAndReverseDividendTransactions() {
        if (this.firstBuyDate == null) {
            return;
        }
        Iterator<DividendTransaction> iter = this.divTransactions.iterator();
        while (iter.hasNext()) {
            DividendTransaction dividendTransaction = iter.next();
            if (dividendTransaction.getExpectedDate().isBefore(this.firstBuyDate)) {
                iter.remove();
            }
        }

        Collections.reverse(this.divTransactions);
    }

    public void matchDividendTransaction(Transaction transaction) {
        for (DividendTransaction dividendTransaction : this.divTransactions) {
            if (dividendTransaction.getMatchingTransaction() != null) {
                continue;
            }
            //the next transaction should match
            Period diff = Period.between(dividendTransaction.getExpectedDate(), transaction.getDate());
            if (diff.getYears() == 0 && diff.getMonths() == 0 && diff.getDays() <= 3 && diff.getDays() >= -3 ) {
                dividendTransaction.setMatchingTransaction(transaction);
            }
        }
    }

    public void reset() {
        this.transactions.clear();
        this.divTransactions.clear();
    }

    public enum DivFrequency {
        NONE, MONTHLY, QUARTELY, SEMIANUAL, ANNUAL
    }


}
