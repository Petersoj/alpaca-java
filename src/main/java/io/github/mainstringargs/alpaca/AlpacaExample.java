package io.github.mainstringargs.alpaca;

import io.github.mainstringargs.alpaca.enums.ActivityType;
import io.github.mainstringargs.alpaca.enums.AssetStatus;
import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderStatus;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.rest.exception.AlpacaAPIRequestException;
import io.github.mainstringargs.alpaca.websocket.AlpacaStreamListenerAdapter;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;
import io.github.mainstringargs.alpaca.websocket.message.account.AccountUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.trade.TradeUpdateMessage;
import io.github.mainstringargs.domain.alpaca.account.Account;
import io.github.mainstringargs.domain.alpaca.accountactivities.AccountActivity;
import io.github.mainstringargs.domain.alpaca.accountconfiguration.AccountConfiguration;
import io.github.mainstringargs.domain.alpaca.asset.Asset;
import io.github.mainstringargs.domain.alpaca.bar.Bar;
import io.github.mainstringargs.domain.alpaca.calendar.Calendar;
import io.github.mainstringargs.domain.alpaca.clock.Clock;
import io.github.mainstringargs.domain.alpaca.order.CancelledOrder;
import io.github.mainstringargs.domain.alpaca.order.Order;
import io.github.mainstringargs.domain.alpaca.position.Position;
import io.github.mainstringargs.domain.alpaca.watchlist.Watchlist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

        // Get Account Activities
        try {
            ArrayList<AccountActivity> accountActivities = alpacaApi.getAccountActivities(
                    null, null, null, Direction.DESC, 3, null, (ActivityType[]) null);

            System.out.println("\n\nAccount Activities:");
            System.out.println("\t" + accountActivities.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get Account Configurations
        AccountConfiguration accountConfiguration = null;
        try {
            accountConfiguration = alpacaApi.getAccountConfiguration();

            System.out.println("\n\nAccount Configuration:");
            System.out.println("\t" + accountConfiguration.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Set Account Configurations
        try {
            accountConfiguration.setTradeConfirmEmail("none");

            AccountConfiguration newAccountConfiguration = alpacaApi.setAccountConfiguration(accountConfiguration);

            System.out.println("\n\nNew Account Configuration:");
            System.out.println("\t" + newAccountConfiguration.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get Open Orders
        try {
            ArrayList<Order> openOrders = alpacaApi.getOrders(OrderStatus.OPEN, null, null, null, null);

            System.out.println("\n\nOpen Orders:");
            System.out.println("\t" + openOrders.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Request an Order
        Order aaplOrder = null;
        try {
            aaplOrder = alpacaApi.requestNewOrder("AAPL", 1, OrderSide.BUY, OrderType.LIMIT, OrderTimeInForce.GTC,
                    201.30, null, true, null);

            System.out.println("\n\nNew AAPL Order:");
            System.out.println("\t" + aaplOrder.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get an order by ID
        try {
            Order aaplOrderByID = alpacaApi.getOrder(aaplOrder.getId());

            System.out.println("\n\nAAPL Order by ID:");
            System.out.println("\t" + aaplOrderByID.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get an order by client ID
        try {
            Order aaplOrderByClientID = alpacaApi.getOrderByClientID(aaplOrder.getClientOrderId());

            System.out.println("\n\nAAPL Order by ID:");
            System.out.println("\t" + aaplOrderByClientID.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Replace an order
        try {
            Order replacedAAPLOrder = alpacaApi.replaceOrder(aaplOrder.getId(), null, OrderTimeInForce.DAY, null, null,
                    null);

            System.out.println("\n\nReplaced AAPL Order:");
            System.out.println("\t" + replacedAAPLOrder.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Cancel all orders
        try {
            ArrayList<CancelledOrder> cancelledOrders = alpacaApi.cancelAllOrders();

            System.out.println("\n\nCancelled Orders:");
            System.out.println("\t" + cancelledOrders.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Cancel all orders
        try {
            ArrayList<CancelledOrder> cancelledOrders = alpacaApi.cancelAllOrders();

            System.out.println("\n\nCancelled Orders:");
            System.out.println("\t" + cancelledOrders.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Cancel a single order
        try {
            boolean orderWasCancelled = alpacaApi.cancelOrder(aaplOrder.getId());

            System.out.println("\n\nCancelled AAPL Order:");
            System.out.println("\t" + orderWasCancelled);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get open positions
        try {
            ArrayList<Position> openPositions = alpacaApi.getOpenPositions();

            System.out.println("\n\nOpen Positions:");
            System.out.println("\t" + openPositions.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get open position by symbol
        try {
            Position openPositionBySymbol = alpacaApi.getOpenPositionBySymbol("AAPL");

            System.out.println("\n\nOpen Position By Symbol:");
            System.out.println("\t" + openPositionBySymbol.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get assets
        try {
            ArrayList<Asset> assets = alpacaApi.getAssets(AssetStatus.ACTIVE, "us_equity");

            System.out.println("\n\nAlpaca Assets:");
            System.out.println("\t" + assets.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get assets
        try {
            ArrayList<Asset> assets = alpacaApi.getAssets(AssetStatus.ACTIVE, "us_equity");

            System.out.println("\n\nAlpaca Assets:");
            System.out.println("\t" + assets.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get asset
        try {
            Asset asset = alpacaApi.getAssetBySymbol("AAPL");

            System.out.println("\n\nAsset by Symbol AAPL:");
            System.out.println("\t" + asset.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get watchlists
        try {
            ArrayList<Watchlist> watchlists = alpacaApi.getWatchlists();

            System.out.println("\n\nWatchlists:");
            System.out.println("\t" + watchlists.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Create watchlist
        Watchlist dayTradeWatchlist = null;
        try {
            dayTradeWatchlist = alpacaApi.createWatchlist("Day Trade", "AAPL");

            System.out.println("\n\nDay Trade Watchlist:");
            System.out.println("\t" + dayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get watchlist
        try {
            Watchlist getDayTradeWatchlist = alpacaApi.getWatchlist(dayTradeWatchlist.getId());

            System.out.println("\n\nGot Day Trade Watchlist:");
            System.out.println("\t" + getDayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Update watchlist
        try {
            Watchlist updatedDayTradeWatchlist = alpacaApi.updateWatchlist(dayTradeWatchlist.getId(),
                    "Day Trade with MSFT", "AAPL", "MSFT");

            System.out.println("\n\nUpdated Day Trade Watchlist:");
            System.out.println("\t" + updatedDayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Add watchlist asset
        try {
            Watchlist addTSLADayTradeWatchlist = alpacaApi.addWatchlistAssets(dayTradeWatchlist.getId(), "TSLA");

            System.out.println("\n\nAdd TSLA Day Trade Watchlist:");
            System.out.println("\t" + addTSLADayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Delete a watchlist
        try {
            boolean deletedDayTradeWatchlist = alpacaApi.deleteWatchlist(dayTradeWatchlist.getId());

            System.out.println("\n\nDelete Day Trade Watchlist:");
            System.out.println("\t" + deletedDayTradeWatchlist);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Remove watchlist symbol
        try {
            Watchlist removeTSLADayTradeWatchlist = alpacaApi.removeSymbolFromWatchlist(dayTradeWatchlist.getId(),
                    "TSLA");

            System.out.println("\n\nRemove TSLA Day Trade Watchlist:");
            System.out.println("\t" + removeTSLADayTradeWatchlist.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get Calendar
        try {
            ArrayList<Calendar> calendars = alpacaApi.getCalendar(LocalDate.of(2019, 11, 25),
                    LocalDate.of(2019, 11, 29));

            System.out.println("\n\nCalendar from 11/25/19 to 11/29/19:");
            System.out.println("\t" + calendars.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get Clock
        try {
            Clock clock = alpacaApi.getClock();

            System.out.println("\n\nCurrent market clock:");
            System.out.println("\t" + clock.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get bars
        try {
            ArrayList<Bar> bars = alpacaApi.getBars(BarsTimeFrame.DAY, "AAPL", null,
                    LocalDateTime.of(2019, 11, 18, 0, 0), LocalDateTime.of(2019, 11, 22, 23, 59), null, null);

            System.out.println("\n\nBars response:");
            System.out.println("\t" + bars.toString().replace(",", ",\n\t"));
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }
}
