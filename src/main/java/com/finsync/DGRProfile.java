package com.finsync;

import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DGRProfile {
    private String ticker;
    private double dgr5y;
    private double dgrLast;
    private DGRRating rating5y;
    private DGRRating ratingLast;
    List<DGREvent> dgrHistory = new ArrayList<>();
    //List<DGREvent>
    Document document;

    public DGRProfile(String ticker) {
        this.ticker = ticker;
    }

    public void addDGREvent(DGREvent dgrEvent) {
        dgrHistory.add(dgrEvent);
    }

    public void setDgr5y(double dgr5y) {
        this.dgr5y = dgr5y;
    }

    public void setDgrLast(double dgrLast) {
        this.dgrLast = dgrLast;
    }

    public void setRating5y (DGRRating rating) {
        this.rating5y = rating;
    }

    public void setRatingLast (DGRRating rating) {
        this.ratingLast = rating;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getTicker() {
        return this.ticker;
    }

    public double getDgr5y () {
        return dgr5y;
    }

    public double getDgrLast () {
        return dgrLast;
    }

    public Document getDocument() {
        return this.document;
    }

    public DGRRating getRating5y() {
        return this.rating5y;
    }

    public DGRRating getRatingLast() {
        return this.ratingLast;
    }

    public List<DGREvent> getDgrHistory() {
        return dgrHistory;
    }

    public void reverseDgrHistory() {
        Collections.reverse(dgrHistory);
    }

    public void updateProfiles() {
        boolean first = true;
        LocalDate cDate = null, lDate = null;
        double dgrChange = 0;
        int[] ratingBins = new int[DGRRating.MAX.ordinal()];

        for (DGREvent dgrEvent: dgrHistory) {
            if (dgrEvent.rating == DGRRating.NONE) {
                continue;
            }
            if (first) {
                cDate = dgrEvent.divExDate;
                this.dgrLast = dgrEvent.growthPercentage;
                this.ratingLast = dgrEvent.rating;
                first = false;
            }
            lDate = dgrEvent.divExDate;
            dgrChange += dgrEvent.growthPercentage;
            ratingBins[dgrEvent.rating.ordinal()]++;
        }

        Period period = Period.between(lDate, cDate);
        this.dgr5y = (dgrChange) / (period.getYears());

        if (dgr5y == 0.0) {
            rating5y = DGRRating.STALLED;
        } else if (this.dgr5y < 0.0) {
            rating5y = DGRProfile.DGRRating.CUT;
        } else if (dgr5y > 0.0 && dgr5y < 4.0) {
            rating5y = DGRProfile.DGRRating.LOW;
        } else if (dgr5y >= 4.0 && dgr5y < 8.0){
            rating5y = DGRProfile.DGRRating.MEDIUM;
        } else {
            rating5y = DGRProfile.DGRRating.HIGH;
        }

        //ignore the currency fluctuations or etf distributions change year-to-year, rather use the 5yr change as indicator of DGR
		if (StockUtil.isForeignOrFunds(ticker)) {
        	this.ratingLast = this.rating5y;
		}
        /*
        if (dgrHistory.get(0).growthPercentage != 0.0 ) {
            this.dgrLast = dgrHistory.get(0).growthPercentage;
        } else {
            this.dgrLast = dgrHistory.get(1).growthPercentage;
        }

        int max = 0;
        for (int i = 0; i < DGRRating.MAX.ordinal(); i++) {
            if (max < ratingBins[i]) {
                max = ratingBins[i];
                this.rating = DGRRating.values()[i];
            }
        }
        */
    }

    public void resetProfile() {
        this.rating5y = DGRRating.NONE;
        this.ratingLast = DGRRating.NONE;
        this.dgrHistory.clear();
    }

    public static final class DGREvent {
        LocalDate divExDate;
        LocalDate divPayDate;
        double divPrevAmount;
        double divAmount;
        double growthPercentage;
        long daysBetween;
        DGRRating rating;
        boolean split = false;

        public DGREvent() {

        }
        public DGREvent setDivExDate(LocalDate divExDate) {
            this.divExDate = divExDate;
            return this;
        }

        public LocalDate getDivExDate() {
            return this.divExDate;
        }

        public DGREvent setDivPayDate(LocalDate divPayDate) {
            this.divPayDate = divPayDate;
            return this;
        }

        public LocalDate getDivPayDate() {
            return this.divPayDate;
        }

        public DGREvent setDivPrevAmount(double divPrevAmount) {
            this.divPrevAmount = divPrevAmount;
            return this;
        }

        public double getDivPrevAmount() {
            return this.divPrevAmount;
        }

        public DGREvent setDivAmount(double divAmount) {
            this.divAmount = divAmount;
            return this;
        }

        public double getDivAmount() {
            return this.divAmount;
        }

        public DGREvent setGrowthPercentage (double growthPercentage) {
            this.growthPercentage = growthPercentage;
            return this;
        }

        public double getGrowthPercentage() {
            return this.growthPercentage;
        }

        public DGREvent setDaysBetween (long daysBetween) {
            this.daysBetween = daysBetween;
            return this;
        }

        public long getDaysBetween() {
            return this.daysBetween;
        }

        public DGREvent setRating (DGRRating rating) {
            this.rating = rating;
            return this;
        }

        public DGRRating getRating() {
            return this.rating;
        }

        public DGREvent setSplit(boolean split) {
            this.split = split;
            return this;
        }

        public boolean getSplit() {
            return this.split;
        }
    }

    public enum DGRRating {
        NONE, HIGH, MEDIUM, LOW, STALLED, CUT, MAX
    }
}
