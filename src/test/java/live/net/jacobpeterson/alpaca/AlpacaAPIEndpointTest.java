package live.net.jacobpeterson.alpaca;

import net.jacobpeterson.abstracts.enums.SortDirection;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.enums.ActivityType;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;
import net.jacobpeterson.domain.alpaca.account.Account;
import net.jacobpeterson.domain.alpaca.accountactivities.AccountActivity;
import net.jacobpeterson.domain.alpaca.clock.Clock;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link AlpacaAPIEndpointTest} tests live endpoints using Alpaca Paper credentials given in the
 * <code>alpaca.properties</code> file on the classpath.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AlpacaAPIEndpointTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlpacaAPIEndpointTest.class);
    private static final int RATE_LIMIT_MILLIS = 200; // Wait 200ms between every test to prevent rate-limiting
    private static final ZoneId NEW_YORK_ZONE_ID = ZoneId.of("America/New_York");

    static {
        // Log trace-level
        System.setProperty("org.slf4j.simpleLogger.log.net.jacobpeterson", "trace");
        System.setProperty("org.slf4j.simpleLogger.log.live.net.jacobpeterson", "trace");
    }

    private static boolean marketOpen;

    /**
     * Executed before all tests in this class.
     */
    @BeforeAll
    public static void beforeAll() {
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
     * Tests {@link AlpacaAPI#getCalendar()}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    @Order(1)
    public void testGetClock() throws AlpacaAPIRequestException {
        AlpacaAPI alpacaAPI = new AlpacaAPI();

        Clock clock = alpacaAPI.getClock();
        LOGGER.debug(clock.toString());

        // Assert basic data integrity and not null
        assertNotNull(clock);
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
    @Order(2)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void testGetAccount() throws AlpacaAPIRequestException, NumberFormatException {
        AlpacaAPI alpacaAPI = new AlpacaAPI();

        Account account = alpacaAPI.getAccount();
        LOGGER.debug(account.toString());

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
     * String, ActivityType...)}.
     *
     * @throws AlpacaAPIRequestException thrown for {@link AlpacaAPIRequestException}s
     */
    @Test
    @Order(3)
    public void testGetAccountActivities() throws AlpacaAPIRequestException {
        assumeTrue(marketOpen, "Market is not open. Skipping!");

        AlpacaAPI alpacaAPI = new AlpacaAPI();

        // List<AccountActivity> accountActivities = alpacaAPI.getAccountActivities(
        //
        // );
    }
}
