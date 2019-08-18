package com.finsync;
import com.github.scribejava.core.builder.*;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.*;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllyApi {
    private final static Logger LOG = LoggerFactory.getLogger(AllyApi.class);

    private static String CONSUMER_KEY = "";
    private static String CONSUMER_SECRET = "";
    private static String OAUTH_TOKEN = "";
    private static String OAUTH_TOKEN_SECRET = "";

    public static final String BASE_URL = "https://api.tradeking.com/v1/";
    public static final String ACCOUNT_BASE_URL = "accounts/";
    public static final String ACCOUNT_URL = "";
    public static final String HISTORY_URL = "/history";
    public static final String HOLDINGS_URL = "/holdings";
    public static final String FORMAT = ".json";

    public static final String QUOTE_BASE_URL = "market/ext/quotes.json?symbols=";

    private static OAuth10aService service;
    private static OAuth1AccessToken accessToken;

    private static int reqCount = 0;


    //public AllyApi() {
    /*
    static {
        service = new ServiceBuilder(CONSUMER_KEY)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build(new TradeKingApi());
        accessToken = new OAuth1AccessToken(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);
    }
    */

    public static void setUpAlly(String consumerKey, String consumerSecret, String oauth_token, String oauthTokenSecret) {
        CONSUMER_KEY = consumerKey;
        CONSUMER_SECRET = consumerSecret;
        OAUTH_TOKEN = oauth_token;
        OAUTH_TOKEN_SECRET = oauthTokenSecret;

        service = new ServiceBuilder(CONSUMER_KEY)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build(new TradeKingApi());
        accessToken = new OAuth1AccessToken(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);

    }

    /*
    public static void rest_execute(String url, String fileName) {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(fileName);
        } catch (Exception e) {

        }
        pw = new PrintWriter(fw);

        String jsonResp = rest_execute(url);

        try {
            pw.print(jsonResp);
        } catch (Exception e) {

        }
        pw.close();
    }

*/
    public static String rest_execute(String url, Writer out) {
        reqCount++;
        if (reqCount % 30 == 0) {
            try {
                Thread.sleep(30000);
            } catch (Exception e) {

            }
        }
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        Response response = null;
        try {

            response = service.execute(request);
            String body = response.getBody();
            if (out != null) {
                out.write(body);
            }
            return body;
        } catch (Exception e) {
            return null;
        }
    }


    public static String buildAccountUrl (String accountId, String endUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append(ACCOUNT_BASE_URL).append(accountId).append(endUrl).append(FORMAT);
        return sb.toString();
    }


    public static String buildQuoteUrl (String tickers) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append(QUOTE_BASE_URL).append(tickers);
        return sb.toString();
    }

    public static void main(String[] args)
    {

        //AllyApi allyApi = new AllyApi();

        //profile
        //rest_execute(service, accessToken, "./profile.json", PROFILE_URL);

        //aapl stock quote
        //rest_execute(service, accessToken, "./quote_appl.json", QUOTE_URL);
        //rest_execute(service, accessToken, "./account.json", ACCOUNT_URL);
        //rest_execute(service, accessToken, "./balances.json", BALANCE_URL);
        //rest_execute(service, accessToken, "./history.json", HISTORY_URL);
        //rest_execute(service, accessToken, "./holdings.json", HOLDINGS_URL);

        try {
            //Account account = new Account("ally", "AAAAAAAA");
            // 09/16/2014 transaction ACH deposit begins the new current investment spell
            //account.setLastTransactionDate(LocalDate.of(2014, 9, 15));
            //account.refreshAll();
        } catch (Exception e) {
            //LOG.error("exception: " , e);

        }
    }

}
