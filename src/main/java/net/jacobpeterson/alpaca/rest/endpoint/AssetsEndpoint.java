package net.jacobpeterson.alpaca.rest.endpoint;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.asset.Asset;
import net.jacobpeterson.alpaca.model.endpoint.asset.enums.AssetStatus;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AbstractEndpoint} for <a href="https://docs.alpaca.markets/api-documentation/api-v2/assets/">Assets</a>.
 */
public class AssetsEndpoint extends AbstractEndpoint {

    /**
     * Instantiates a new {@link AssetsEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public AssetsEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "assets");
    }

    /**
     * Get a list of {@link Asset}s.
     *
     * @param assetStatus the {@link AssetStatus}. By default, all {@link AssetStatus}es are included.
     * @param assetClass  the asset class. Defaults to "us_equity".
     *
     * @return a {@link List} of {@link Asset}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Asset> get(AssetStatus assetStatus, String assetClass) throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        if (assetStatus != null) {
            urlBuilder.addQueryParameter("status", assetStatus.toString());
        }

        if (assetClass != null) {
            urlBuilder.addQueryParameter("asset_class", assetClass);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, new TypeToken<ArrayList<Asset>>() {}.getType());
    }

    /**
     * Gets an {@link Asset} by a symbol or {@link Asset#getId()}.
     *
     * @param symbolOrAssetID the symbol or {@link Asset#getId()}
     *
     * @return the {@link Asset}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Asset getBySymbol(String symbolOrAssetID) throws AlpacaClientException {
        checkNotNull(symbolOrAssetID);

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(symbolOrAssetID);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Asset.class);
    }
}
