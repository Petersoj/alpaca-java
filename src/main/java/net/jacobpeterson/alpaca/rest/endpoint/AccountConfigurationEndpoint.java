package net.jacobpeterson.alpaca.rest.endpoint;

import net.jacobpeterson.alpaca.model.endpoint.accountconfiguration.AccountConfiguration;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.util.okhttp.JSONBodyBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.jacobpeterson.alpaca.util.gson.GsonUtil.GSON;

/**
 * {@link AbstractEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/account-configuration/">Account Configuration</a>.
 */
public class AccountConfigurationEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link AccountConfigurationEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public AccountConfigurationEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "account/configurations");
    }

    /**
     * Returns the {@link AccountConfiguration}.
     *
     * @return the {@link AccountConfiguration}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public AccountConfiguration get() throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegments(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, AccountConfiguration.class);
    }

    /**
     * Sets the {@link AccountConfiguration}.
     *
     * @param accountConfiguration the {@link AccountConfiguration}
     *
     * @return the updated {@link AccountConfiguration}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public AccountConfiguration set(AccountConfiguration accountConfiguration) throws AlpacaClientException {
        checkNotNull(accountConfiguration);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegments(endpointPathSegment);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .patch(new JSONBodyBuilder(GSON.toJson(accountConfiguration)).build())
                .build();
        return alpacaClient.requestObject(request, AccountConfiguration.class);
    }
}
