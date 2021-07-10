package net.jacobpeterson.alpaca.refactor.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.jacobpeterson.alpaca.refactor.properties.AlpacaProperties;
import okhttp3.*;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.jacobpeterson.alpaca.refactor.util.gson.GsonUtil.GSON;

/**
 * {@link AlpacaClient} represents an HTTP RestAPI client for Alpaca.
 */
public class AlpacaClient {

    private final OkHttpClient okHttpClient;
    private final HttpUrl baseURL;
    private final Headers requestHeaders;

    /**
     * Instantiates a new {@link AlpacaClient}.
     *
     * @param okHttpClient       the {@link OkHttpClient}
     * @param keyID              the key ID
     * @param secretKey          the secret key
     * @param alpacaSubdomain    the Alpaca subdomain
     * @param versionPathSegment the version path segment e.g. "v2"
     */
    public AlpacaClient(OkHttpClient okHttpClient, String keyID, String secretKey, String alpacaSubdomain,
            String versionPathSegment) {
        this(okHttpClient, keyID, secretKey, null, alpacaSubdomain, versionPathSegment);
    }

    /**
     * Instantiates a new {@link AlpacaClient}.
     *
     * @param okHttpClient       the {@link OkHttpClient}
     * @param oAuthToken         the OAuth token
     * @param alpacaSubdomain    the Alpaca subdomain
     * @param versionPathSegment the version path segment e.g. "v2"
     */
    public AlpacaClient(OkHttpClient okHttpClient, String oAuthToken, String alpacaSubdomain,
            String versionPathSegment) {
        this(okHttpClient, null, null, oAuthToken, alpacaSubdomain, versionPathSegment);
    }

    /**
     * Instantiates a new {@link AlpacaClient}.
     *
     * @param okHttpClient       the {@link OkHttpClient}
     * @param keyID              the key ID
     * @param secretKey          the secret key
     * @param oAuthToken         the OAuth token
     * @param alpacaSubdomain    the Alpaca subdomain
     * @param versionPathSegment the version path segment e.g. "v2"
     */
    protected AlpacaClient(OkHttpClient okHttpClient, String keyID, String secretKey, String oAuthToken,
            String alpacaSubdomain, String versionPathSegment) {
        checkNotNull(okHttpClient);
        checkArgument((keyID != null && secretKey != null) ^ oAuthToken != null,
                "You must specify a (KeyID and Secret Key) or an OAuthToken!");
        checkNotNull(alpacaSubdomain);
        checkNotNull(versionPathSegment);

        this.okHttpClient = okHttpClient;

        baseURL = new HttpUrl.Builder()
                .scheme("https")
                .host(alpacaSubdomain + ".alpaca.markets")
                .addPathSegment(versionPathSegment)
                .build();

        Headers.Builder requestHeadersBuilder = new Headers.Builder();
        requestHeadersBuilder.add("User-Agent", AlpacaProperties.USER_AGENT);
        if (oAuthToken != null) {
            requestHeadersBuilder.add("Authorization", "Bearer " + oAuthToken);
        } else {
            requestHeadersBuilder
                    .add("APCA-API-KEY-ID", keyID)
                    .add("APCA-API-SECRET-KEY", secretKey);
        }
        requestHeaders = requestHeadersBuilder.build();
    }

    /**
     * Gets a new {@link HttpUrl.Builder}.
     *
     * @return a {@link HttpUrl.Builder}
     */
    public HttpUrl.Builder urlBuilder() {
        return baseURL.newBuilder();
    }

    /**
     * Gets a new {@link Request.Builder} with {@link #requestHeaders}.
     *
     * @param httpUrl the {@link HttpUrl}
     *
     * @return a {@link Request.Builder}
     */
    public Request.Builder requestBuilder(HttpUrl httpUrl) {
        return new Request.Builder().headers(requestHeaders).url(httpUrl);
    }

    /**
     * Calls {@link #requestObject(Request, Predicate, Type)} with a success code of <code>200</code>.
     */
    public <T> T requestObject(Request request, Type type) throws AlpacaClientException {
        return requestObject(request, (code) -> code == 200, type);
    }

    /**
     * Requests an object given a {@link Request} that is deserialized via {@link Gson#fromJson(Reader, Type)}.
     *
     * @param <T>           the type of object
     * @param request       the {@link Request}
     * @param isSuccessCode throws {@link AlpacaClientException} if passed in {@link Response#code()} to the {@link
     *                      Predicate#test(Object)} returns false
     * @param type          the object {@link Type}
     *
     * @return the requested object
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public <T> T requestObject(Request request, Predicate<Integer> isSuccessCode, Type type)
            throws AlpacaClientException {
        return GSON.fromJson(requestJSON(request, isSuccessCode), type);
    }

    /**
     * Calls {@link #requestJSON(Request, Predicate)} with a success code of <code>200</code>.
     */
    public JsonElement requestJSON(Request request) throws AlpacaClientException {
        return requestJSON(request, (code) -> code == 200);
    }

    /**
     * Requests a {@link JsonElement} given a {@link Request}.
     *
     * @param request       the {@link Request}
     * @param isSuccessCode throws {@link AlpacaClientException} if passed in {@link Response#code()} to the {@link
     *                      Predicate#test(Object)} returns false
     *
     * @return the {@link JsonElement}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public JsonElement requestJSON(Request request, Predicate<Integer> isSuccessCode) throws AlpacaClientException {
        return JsonParser.parseReader(request(request, isSuccessCode).body().charStream());
    }

    /**
     * Sends a {@link Request} and returns a {@link Response}.
     *
     * @param request       the {@link Request}
     * @param isSuccessCode throws {@link AlpacaClientException} if passed in {@link Response#code()} to the {@link
     *                      Predicate#test(Object)} returns false
     *
     * @return the {@link Response}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Response request(Request request, Predicate<Integer> isSuccessCode) throws AlpacaClientException {
        try {
            Response response = executeRequest(request);
            if (!isSuccessCode.test(response.code())) {
                throw new AlpacaClientException(response);
            }
            return response;
        } catch (Exception exception) {
            throw new AlpacaClientException(exception);
        }
    }

    /**
     * Executes a {@link Request}.
     *
     * @param request the {@link Request}
     *
     * @return the {@link Response}
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public Response executeRequest(Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response;
        }
    }

    /**
     * Gets {@link #baseURL}.
     *
     * @return the base {@link HttpUrl}
     */
    public HttpUrl getBaseURL() {
        return baseURL;
    }

    /**
     * Gets {@link #requestHeaders}.
     *
     * @return the request {@link Headers}
     */
    public Headers getRequestHeaders() {
        return requestHeaders;
    }
}
