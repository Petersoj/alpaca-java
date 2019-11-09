package io.github.mainstringargs.alpaca;

import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.rest.exceptions.AlpacaAPIRequestException;
import io.github.mainstringargs.alpaca.websocket.AlpacaStreamListenerAdapter;
import io.github.mainstringargs.alpaca.websocket.message.AccountUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.OrderUpdateMessage;
import io.github.mainstringargs.alpaca.websocket.message.UpdateMessage;
import io.github.mainstringargs.domain.alpaca.account.Account;
import io.github.mainstringargs.domain.alpaca.bar.Bar;
import io.github.mainstringargs.domain.alpaca.clock.Clock;
import io.github.mainstringargs.domain.alpaca.order.Order;
import io.github.mainstringargs.util.time.TimeUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/**
 * The Class Example.
 */
public class AlpacaExample {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // This logs into Alpaca using the alpaca.properties file on the classpath.
        AlpacaAPI alpacaApi = new AlpacaAPI();

        // Register explicitly for ACCOUNT_UPDATES and ORDER_UPDATES Messages via stream listener
        alpacaApi.addAlpacaStreamListener(new AlpacaStreamListenerAdapter(MessageType.ACCOUNT_UPDATES,
                MessageType.ORDER_UPDATES) {
            @Override
            public void streamUpdate(MessageType messageType, UpdateMessage message) {

                switch (messageType) {
                    case ACCOUNT_UPDATES:
                        AccountUpdateMessage accounUpdateMessage = (AccountUpdateMessage) message;
                        System.out
                                .println("\nReceived Account Update: \n\t" + accounUpdateMessage.toString());
                        break;
                    case ORDER_UPDATES:
                        OrderUpdateMessage orderUpdateMessage = (OrderUpdateMessage) message;
                        System.out.println("\nReceived Order Update: \n\t" + orderUpdateMessage.toString());
                        break;
                }
            }
        });

        // Get Account Information
        try {
            Account alpacaAccount = alpacaApi.getAccount();

            System.out.println("\n\nAccount Information:");
            System.out.println("\tCreated At: " + TimeUtil.fromDateTimeString(alpacaAccount.getCreatedAt()) +
                    "\n\tBuying Power: " + alpacaAccount.getBuyingPower() +
                    "\n\tPortfolio Value: " + alpacaAccount.getPortfolioValue());
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get Stock Market Hours
        try {
            Clock alpacaClock = alpacaApi.getClock();

            System.out.println("\n\nClock:");
            System.out.println("\tCurrent Time: " + alpacaClock.getTimestamp() +
                    "\n\tIs Open: " + alpacaClock.isIsOpen() +
                    "\n\tMarket Next Open Time: " + alpacaClock.getNextOpen() +
                    "\n\tMark Next Close Time: " + alpacaClock.getNextClose());
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        Order limitOrder = null;
        String orderClientId = UUID.randomUUID().toString();

        // Request an Order
        try {
            // Lets submit a limit order for when AMZN gets down to $10.0!
            limitOrder = alpacaApi.requestNewOrder("AMZN", 1, OrderSide.BUY, OrderType.LIMIT,
                    OrderTimeInForce.DAY, 10.0, null, orderClientId);

            System.out.println("\n\nLimit Order Response:");
            System.out.println("\tSymbol: " + limitOrder.getSymbol() +
                    "\n\tClient Order Id: " + limitOrder.getClientOrderId() +
                    "\n\tQty: " + limitOrder.getQty() +
                    "\n\tType: " + limitOrder.getType() +
                    "\n\tLimit Price: $" + limitOrder.getLimitPrice() +
                    "\n\tCreated At: " + limitOrder.getCreatedAt());
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get an existing Order by Id
        try {
            Order limitOrderById = alpacaApi.getOrder(limitOrder.getId());

            System.out.println("\n\nLimit Order By Id Response:");
            System.out.println("\tSymbol: " + limitOrderById.getSymbol() +
                    "\n\tClient Order Id: " + limitOrderById.getClientOrderId() +
                    "\n\tQty: " + limitOrderById.getQty() +
                    "\n\tType: " + limitOrderById.getType() +
                    "\n\tLimit Price: $" + limitOrderById.getLimitPrice() +
                    "\n\tCreated At: " + limitOrderById.getCreatedAt());
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get an existing Order by Client Id
        try {
            Order limitOrderByClientId = alpacaApi.getOrderByClientId(limitOrder.getClientOrderId());

            System.out.println("\n\nLimit Order By Id Response:");
            System.out.println("\tSymbol: " + limitOrderByClientId.getSymbol() +
                    "\n\tClient Order Id: " + limitOrderByClientId.getClientOrderId() +
                    "\n\tQty: " + limitOrderByClientId.getQty() +
                    "\n\tType: " + limitOrderByClientId.getType() +
                    "\n\tLimit Price: $" + limitOrderByClientId.getLimitPrice() +
                    "\n\tCreated At: " + limitOrderByClientId.getCreatedAt());
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Cancel the existing order
        try {
            boolean orderCanceled = alpacaApi.cancelOrder(limitOrder.getId());

            System.out.println("\n\nCancel order response:");
            System.out.println("\tCancelled: " + orderCanceled);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        // Get bars
        try {
            List<Bar> bars = alpacaApi.getBars(BarsTimeFrame.ONE_DAY, "AMZN", 10,
                    LocalDateTime.of(2019, 2, 13, 10, 30), LocalDateTime.of(2019, 2, 14, 10, 30), null, null);

            System.out.println("\n\nBars response:");

            for (Bar bar : bars) {
                System.out.println("\t==========");
                System.out.println("\tUnix Time "
                        + LocalDateTime.ofInstant(Instant.ofEpochMilli(bar.getT() * 1000), ZoneId.of("UTC")));
                System.out.println("\tOpen: $" + bar.getO());
                System.out.println("\tHigh: $" + bar.getH());
                System.out.println("\tLow: $" + bar.getL());
                System.out.println("\tClose: $" + bar.getC());
                System.out.println("\tVolume: " + bar.getV());
            }
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
