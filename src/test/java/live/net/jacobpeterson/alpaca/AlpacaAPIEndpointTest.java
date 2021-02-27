package live.net.jacobpeterson.alpaca;

import net.jacobpeterson.abstracts.enums.SortDirection;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.enums.ActivityType;
import net.jacobpeterson.alpaca.enums.OrderStatus;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;
import net.jacobpeterson.domain.alpaca.account.Account;
import net.jacobpeterson.domain.alpaca.accountactivities.AccountActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.NonTradeActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.TradeActivity;
import net.jacobpeterson.domain.alpaca.accountconfiguration.AccountConfiguration;
import net.jacobpeterson.domain.alpaca.clock.Clock;
import net.jacobpeterson.domain.alpaca.order.Order;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link AlpacaAPIEndpointTest} tests live endpoints using Alpaca Paper credentials given in the
 * <code>alpaca.properties</code> file on the classpath.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlpacaAPIEndpointTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPIEndpointTest.class);
    private static final int RATE_LIMIT_MILLIS = 200; // Wait 200ms between every test to prevent rate-limiting

    static {
        // Log trace-level
        System.setProperty("org.slf4j.simpleLogger.log.net.jacobpeterson", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.live.net.jacobpeterson", "trace");
    }

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
     * Tests {@link AlpacaAPI#getClock()}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    public void test_getClock() throws AlpacaAPIRequestException {
        Clock clock = alpacaAPI.getClock();
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
     * Tests {@link AlpacaAPI#getAccount()}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     * @throws NumberFormatException     thrown for {@link NumberFormatException}s
     */
    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void test_getAccount() throws AlpacaAPIRequestException, NumberFormatException {
        Account account = alpacaAPI.getAccount();
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
     * Tests @{@link AlpacaAPI#getAccountActivities(ZonedDateTime, ZonedDateTime, ZonedDateTime, SortDirection, Integer,
     * String, ActivityType...)} one {@link AccountActivity} exists until now.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    public void test_getAccountActivities_One_Activity_Exists_Until_Now() throws AlpacaAPIRequestException {
        List<AccountActivity> accountActivities = alpacaAPI.getAccountActivities(
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
            assertNotNull(tradeActivity.getCumQty());
            assertNotNull(tradeActivity.getLeavesQty());
            assertNotNull(tradeActivity.getPrice());
            assertNotNull(tradeActivity.getQty());
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
            assertNotNull(nonTradeActivity.getQty());
            assertNotNull(nonTradeActivity.getPerShareAmount());
            assertNotNull(nonTradeActivity.getDescription());
        }
    }

    /**
     * Test {@link AlpacaAPI#getAccountConfiguration()}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    public void test_getAccountConfiguration() throws AlpacaAPIRequestException {
        AccountConfiguration accountConfiguration = alpacaAPI.getAccountConfiguration();
        assertNotNull(accountConfiguration);

        LOGGER.debug("{}", accountConfiguration);

        assertNotNull(accountConfiguration.getDtbpCheck());
        assertNotNull(accountConfiguration.getTradeConfirmEmail());
        assertNotNull(accountConfiguration.getSuspendTrade());
        assertNotNull(accountConfiguration.getNoShorting());

        AlpacaAPIEndpointTest.accountConfiguration = accountConfiguration;
    }

    /**
     * Test {@link AlpacaAPI#setAccountConfiguration(AccountConfiguration)}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    public void test_setAccountConfiguration() throws AlpacaAPIRequestException {
        if (accountConfiguration == null) {
            AccountConfiguration newAccountConfiguration = new AccountConfiguration(
                    "both",
                    "none",
                    false,
                    false);
            LOGGER.info("Settings Account Configuration to: {}", newAccountConfiguration);
            alpacaAPI.setAccountConfiguration(newAccountConfiguration);
        } else {
            alpacaAPI.setAccountConfiguration(accountConfiguration);
        }
    }

    /**
     * Test {@link AlpacaAPI#getOrders(OrderStatus, Integer, ZonedDateTime, ZonedDateTime, SortDirection, Boolean,
     * List)} one {@link Order} exists until now.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    public void test_getOrders_One_Order_Exists_Until_now() throws AlpacaAPIRequestException {
        List<Order> orders = alpacaAPI.getOrders(
                OrderStatus.ALL,
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
        assertNotNull(order.getFilledAt());
        assertNotNull(order.getAssetId());
        assertNotNull(order.getSymbol());
        assertNotNull(order.getAssetClass());
        assertNotNull(order.getQty());
        assertNotNull(order.getFilledQty());
        assertNotNull(order.getType());
        assertNotNull(order.getSide());
        assertNotNull(order.getTimeInForce());
        assertNotNull(order.getFilledAvgPrice());
        assertNotNull(order.getStatus());
        assertNotNull(order.getExtendedHours());
    }

    @Test
    public void test() {
        assumeTrue(marketOpen, "Market is not open. Skipping!");
    }
}
