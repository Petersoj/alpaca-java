package net.jacobpeterson.alpaca.rest.endpoint;

import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * {@link AbstractEndpoint} for <a href="https://docs.alpaca.markets/api-documentation/api-v2/account/">Account</a>.
 */
public class AccountEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link AccountEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public AccountEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "account");
    }

    /**
     * Returns the {@link Account}.
     *
     * @return the {@link Account}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Account get() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Account.class);
    }
}
