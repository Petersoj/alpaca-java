package io.github.mainstringargs.alpaca;

import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.rest.exception.AlpacaAPIRequestException;
import io.github.mainstringargs.alpaca.websocket.AlpacaStreamListenerAdapter;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;
import io.github.mainstringargs.alpaca.websocket.message.account.AccountUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.trade.TradeUpdateMessage;
import io.github.mainstringargs.domain.alpaca.account.Account;
import io.github.mainstringargs.domain.alpaca.bar.Bar;
import io.github.mainstringargs.domain.alpaca.order.Order;
import io.github.mainstringargs.domain.alpaca.watchlist.Watchlist;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * The type Alpaca example.
 */
public class AlpacaExample {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // This logs into Alpaca using the alpaca.properties file on the classpath.
        AlpacaAPI alpacaApi = new AlpacaAPI();

        // Register explicitly for ACCOUNT_UPDATES and ORDER_UPDATES Messages via stream listener
        alpacaApi.addAlpacaStreamListener(new AlpacaStreamListenerAdapter(
                StreamUpdateType.ACCOUNT_UPDATES,
                StreamUpdateType.TRADE_UPDATES) {
            @Override
            public void streamUpdate(StreamUpdateType streamUpdateType, ChannelMessage message) {
                switch (streamUpdateType) {
                    case ACCOUNT_UPDATES:
                        AccountUpdateMessage accountUpdateMessage = (AccountUpdateMessage) message;
                        System.out.println("\nReceived Account Update: \n\t" + accountUpdateMessage.toString());
                        break;
                    case TRADE_UPDATES:
                        TradeUpdateMessage tradeUpdateMessage = (TradeUpdateMessage) message;
                        System.out.println("\nReceived Order Update: \n\t" + tradeUpdateMessage.toString());
                        break;
                }
            }
        });

        // Get Account Information
        try {
            Account alpacaAccount = alpacaApi.getAccount();

            System.out.println("\n\nAccount Information:");
            System.out.println("\t" + alpacaAccount.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Request an Order
        try {
            Order aaplOrder = alpacaApi.requestNewOrder("AAPL", 1, OrderSide.BUY, OrderType.LIMIT, OrderTimeInForce.GTC,
                    201.30, null, true, null);

            System.out.println("\n\nNew AAPL Order:");
            System.out.println("\t" + aaplOrder.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Create watchlist
        try {
            Watchlist dayTradeWatchlist = alpacaApi.createWatchlist("Day Trade", "AAPL");

            System.out.println("\n\nDay Trade Watchlist:");
            System.out.println("\t" + dayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get bars
        try {
            ZonedDateTime start = ZonedDateTime.of(2019, 11, 18, 0, 0, 0, 0, ZoneId.of("America/New_York"));
            ZonedDateTime end = ZonedDateTime.of(2019, 11, 22, 23, 59, 0, 0, ZoneId.of("America/New_York"));

            Map<String, ArrayList<Bar>> bars = alpacaApi.getBars(BarsTimeFrame.DAY, "AAPL", null, start, end,
                    null, null);

            System.out.println("\n\nBars response:");
            for (Bar bar : bars.get("AAPL")) {
                System.out.println("\t==========");
                System.out.println("\tUnix Time " + ZonedDateTime.ofInstant(Instant.ofEpochSecond(bar.getT()),
                        ZoneOffset.UTC));
                System.out.println("\tOpen: $" + bar.getO());
                System.out.println("\tHigh: $" + bar.getH());
                System.out.println("\tLow: $" + bar.getL());
                System.out.println("\tClose: $" + bar.getC());
                System.out.println("\tVolume: " + bar.getV());
            }
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }
}
