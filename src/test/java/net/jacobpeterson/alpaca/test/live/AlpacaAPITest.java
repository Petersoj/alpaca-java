package net.jacobpeterson.alpaca.test.live;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.AccountActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.NonTradeActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.TradeActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.enums.ActivityType;
import net.jacobpeterson.alpaca.model.endpoint.accountconfiguration.AccountConfiguration;
import net.jacobpeterson.alpaca.model.endpoint.accountconfiguration.enums.DTBPCheck;
import net.jacobpeterson.alpaca.model.endpoint.accountconfiguration.enums.TradeConfirmEmail;
import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.CurrentOrderStatus;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.account.AccountEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.accountconfiguration.AccountConfigurationEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.clock.ClockEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.orders.OrdersEndpoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * {@link AlpacaAPITest} tests live endpoints using Alpaca Paper credentials given in the
 * <code>alpaca.properties</code> file on the classpath.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlpacaAPITest {

    static {
        // Log trace-level
        System.setProperty("org.slf4j.simpleLogger.log.net.jacobpeterson", "trace");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPITest.class);
    private static final int RATE_LIMIT_MILLIS = 200; // Wait 200ms between every test to prevent rate-limiting

    private static AlpacaAPI alpacaAPI;
    private static boolean marketOpen;
    private static AccountConfiguration accountConfiguration;

    /**
     * Executed before all tests in this class.
     */
    @BeforeAll
    public static void beforeAll() {
        alpacaAPI = new AlpacaAPI();
        marketOpen = false;
    }

    /**
     * Executed before each test.
     */
    @BeforeEach
    public void beforeEach() {
    }

    /**
     * Executed after each test. Note that this will {@link Thread#sleep(long)} for {@link #RATE_LIMIT_MILLIS} to
     * protect against rate limiting.
     */
    @AfterEach
    public void afterEach() {
        try {
            Thread.sleep(RATE_LIMIT_MILLIS);
        } catch (InterruptedException exception) {
            fail("Interrupted when tearing down!");
        }
    }

    /**
     * Tests {@link ClockEndpoint#get()}.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    public void testClockEndpointGet() throws AlpacaClientException {
        Clock clock = alpacaAPI.clock().get();
        assertNotNull(clock);

        LOGGER.debug("{}", clock);

        assertNotNull(clock.getTimestamp());
        assertNotNull(clock.getIsOpen());
        assertNotNull(clock.getNextOpen());
        assertNotNull(clock.getNextClose());

        marketOpen = clock.getIsOpen();
        if (marketOpen) {
            LOGGER.info("Market is currently open! All live endpoints will be tested.");
        } else {
            LOGGER.info("Market is currently closed! Only some live endpoints will be tested.");
        }
    }

    /**
     * Tests {@link AccountEndpoint#get()}.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     * @throws NumberFormatException thrown for {@link NumberFormatException}s
     */
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testAccountEndpointGet() throws AlpacaClientException, NumberFormatException {
        Account account = alpacaAPI.account().get();
        assertNotNull(account);

        LOGGER.debug("{}", account);

        // Assert basic data integrity and not null
        Double.parseDouble(account.getCash());
        Double.parseDouble(account.getPortfolioValue());
        Double.parseDouble(account.getLongMarketValue());
        Double.parseDouble(account.getShortMarketValue());
        Double.parseDouble(account.getEquity());
        Double.parseDouble(account.getLastEquity());
        Double.parseDouble(account.getBuyingPower());
        Double.parseDouble(account.getInitialMargin());
        Double.parseDouble(account.getMaintenanceMargin());
        Double.parseDouble(account.getLastMaintenanceMargin());
        Double.parseDouble(account.getDaytradingBuyingPower());
        Double.parseDouble(account.getRegtBuyingPower());

        // Assert other data exists
        assertNotNull(account.getId());
        assertNotNull(account.getAccountNumber());
        assertNotNull(account.getStatus());
        assertNotNull(account.getCurrency());
        assertNotNull(account.getPatternDayTrader());
        assertNotNull(account.getTradeSuspendedByUser());
        assertNotNull(account.getTradingBlocked());
        assertNotNull(account.getTransfersBlocked());
        assertNotNull(account.getAccountBlocked());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getShortingEnabled());
        assertNotNull(account.getMultiplier());
        assertNotNull(account.getSma());
        assertNotNull(account.getDaytradeCount());
    }

    /**
     * Tests
     * @{@link AccountActivitiesEndpoint#get(ZonedDateTime, ZonedDateTime, ZonedDateTime, SortDirection, Integer,
     *String, ActivityType...)} one {@link AccountActivity} exists until now.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    @Test
    public void testAccountActivitiesEndpointGetOneActivityExistsUntilNow() throws AlpacaClientException {
        List<AccountActivity> accountActivities = alpacaAPI.accountActivities().get(
                null,
                ZonedDateTime.now(),
                null,
                SortDirection.ASCENDING,
                1,
                null,
                (ActivityType[]) null);
        assertNotNull(accountActivities);
        assertFalse(accountActivities.isEmpty());

        accountActivities.forEach(accountActivity -> LOGGER.debug(accountActivity.toString()));

        AccountActivity accountActivity = accountActivities.get(0);
        if (accountActivity instanceof TradeActivity) {
            TradeActivity tradeActivity = (TradeActivity) accountActivity;
            assertNotNull(tradeActivity.getActivityType());
            assertNotNull(tradeActivity.getId());
            assertNotNull(tradeActivity.getCumulativeQuantity());
            assertNotNull(tradeActivity.getRemainingQuantity());
            assertNotNull(tradeActivity.getPrice());
            assertNotNull(tradeActivity.getQuantity());
            assertNotNull(tradeActivity.getSide());
            assertNotNull(tradeActivity.getSymbol());
            assertNotNull(tradeActivity.getTransactionTime());
            assertNotNull(tradeActivity.getOrderId());
            assertNotNull(tradeActivity.getType());
        } else if (accountActivity instanceof NonTradeActivity) {
            NonTradeActivity nonTradeActivity = (NonTradeActivity) accountActivity;
            assertNotNull(nonTradeActivity.getActivityType());
            assertNotNull(nonTradeActivity.getId());
            assertNotNull(nonTradeActivity.getDate());
            assertNotNull(nonTradeActivity.getNetAmount());
            assertNotNull(nonTradeActivity.getSymbol());
            assertNotNull(nonTradeActivity.getQuantity());
            assertNotNull(nonTradeActivity.getPerShareAmount());
            assertNotNull(nonTradeActivity.getDescription());
        }
    }

    /**
     * Test {@link AccountConfigurationEndpoint#get()}.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    public void testAccountConfigurationEndpointGet() throws AlpacaClientException {
        AccountConfiguration accountConfiguration = alpacaAPI.accountConfiguration().get();
        assertNotNull(accountConfiguration);

        LOGGER.debug("{}", accountConfiguration);

        assertNotNull(accountConfiguration.getDtbpCheck());
        assertNotNull(accountConfiguration.getTradeConfirmEmail());
        assertNotNull(accountConfiguration.getSuspendTrade());
        assertNotNull(accountConfiguration.getNoShorting());

        AlpacaAPITest.accountConfiguration = accountConfiguration;
    }

    /**
     * Test {@link AccountConfigurationEndpoint#set(AccountConfiguration)}.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    public void testAccountConfigurationEndpointSet() throws AlpacaClientException {
        if (accountConfiguration == null) {
            AccountConfiguration newAccountConfiguration = new AccountConfiguration(
                    DTBPCheck.BOTH,
                    TradeConfirmEmail.NONE,
                    false,
                    false);
            LOGGER.info("Settings Account Configuration to: {}", newAccountConfiguration);
            alpacaAPI.accountConfiguration().set(newAccountConfiguration);
        } else {
            alpacaAPI.accountConfiguration().set(accountConfiguration);
        }
    }

    /**
     * Test
     * {@link OrdersEndpoint#get(CurrentOrderStatus, Integer, ZonedDateTime, ZonedDateTime, SortDirection, Boolean,
     * Collection)} one {@link Order} exists until now.
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    @Test
    public void testOrdersEndpointGetOneOrderExistsUntilNow() throws AlpacaClientException {
        List<Order> orders = alpacaAPI.orders().get(
                CurrentOrderStatus.ALL,
                1,
                null,
                ZonedDateTime.now(),
                SortDirection.ASCENDING,
                true,
                null);

        assertNotNull(orders);
        assertFalse(orders.isEmpty());

        orders.forEach(order -> LOGGER.debug("{}", order));

        // Assert required fields are present
        Order order = orders.get(0);
        assertNotNull(order.getId());
        assertNotNull(order.getClientOrderId());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertNotNull(order.getSubmittedAt());
        assertNotNull(order.getAssetId());
        assertNotNull(order.getSymbol());
        assertNotNull(order.getAssetClass());
        assertNotNull(order.getQuantity());
        assertNotNull(order.getFilledQuantity());
        assertNotNull(order.getType());
        assertNotNull(order.getSide());
        assertNotNull(order.getTimeInForce());
        assertNotNull(order.getStatus());
        assertNotNull(order.getExtendedHours());
    }
}
