package net.jacobpeterson.polygon.rest;

import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.rest.AbstractRequest;
import net.jacobpeterson.abstracts.rest.AbstractRequestBuilder;
import net.jacobpeterson.polygon.properties.PolygonProperties;

import java.io.InputStream;

/**
 * The Class PolygonRequest.
 */
public class PolygonRequest extends AbstractRequest {

    /** The Constant USER_AGENT_KEY. */
    private static final String USER_AGENT_KEY = "user-agent";

    /** The Constant API_KEY_ID. */
    private static final String API_KEY_PARAMETER = "apiKey";

    /** The key id. */
    private final String keyId;

    /**
     * Instantiates a new polygon request.
     *
     * @param keyID the key id
     */
    public PolygonRequest(String keyID) {
        super();
        this.keyId = keyID;

        headers.put(USER_AGENT_KEY, PolygonProperties.USER_AGENT_VALUE);
    }

    @Override
    public HttpResponse<InputStream> invokeGet(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokeGet(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokeHead(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokeHead(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokePost(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokePost(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokePatch(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokePatch(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokePut(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokePut(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokeDelete(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokeDelete(abstractRequestBuilder);
    }

    @Override
    public HttpResponse<InputStream> invokeOptions(AbstractRequestBuilder abstractRequestBuilder) {
        abstractRequestBuilder.appendURLParameter(API_KEY_PARAMETER, keyId);
        return super.invokeOptions(abstractRequestBuilder);
    }
}
