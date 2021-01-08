package mock.net.jacobpeterson.alpaca;

import net.jacobpeterson.alpaca.AlpacaAPI;
import org.junit.jupiter.api.Test;

/**
 * {@link AlpacaAPITest} tests {@link AlpacaAPI} using mocked objects with Mockito.
 */
public class AlpacaAPITest {

    /**
     * Tests {@link AlpacaAPI#AlpacaAPI()}.
     */
    @Test
    public void testAlpacaAPIConstructor_Default() {
        new AlpacaAPI();
    }

    /**
     * Tests {@link AlpacaAPI#AlpacaAPI(String, String)}.
     */
    @Test
    public void testAlpacaAPIConstructor_keyID_secret() {
        String keyID = "ABCDEFGHIJKLM";
        String secret = "NOPQURSTUVWXYZ";

        new AlpacaAPI(keyID, secret);
    }

    /**
     * Tests {@link AlpacaAPI#AlpacaAPI(String, String, String)}.
     */
    @Test
    public void testAlpacaAPIConstructor_keyID_secret_baseAPIURL() {
        String keyID = "ABCDEFGHIJKLM";
        String secret = "NOPQURSTUVWXYZ";
        String baseAPIURL = "https://paper-api.foo.bar";

        new AlpacaAPI(keyID, secret, baseAPIURL);
    }

    /**
     * Tests {@link AlpacaAPI#AlpacaAPI(String)}.
     */
    @Test
    public void testAlpacaAPIConstructor_oAuthToken() {
        String oAuthToken = "ABCDEFGHIJKLMNOPQURSTUVWXYZ";

        new AlpacaAPI(oAuthToken);
    }

    /**
     * Tests {@link AlpacaAPI#AlpacaAPI(String, String, String, String, String)}.
     */
    @Test
    public void testAlpacaAPIConstructor_keyID_secret_oAuthToken_baseAPIURL_baseDataURL() {
        String keyID = "ABCDEFGHIJKLM";
        String secret = "NOPQURSTUVWXYZ";
        String oAuthToken = "ABCDEFGHIJKLMNOPQURSTUVWXYZ";
        String baseAPIURL = "https://paper-api.foo.bar";
        String baseDataURL = "https://data.foo.bar";

        new AlpacaAPI(keyID, secret, oAuthToken, baseAPIURL, baseDataURL);
    }
}
