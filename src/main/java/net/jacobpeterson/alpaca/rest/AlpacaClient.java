package net.jacobpeterson.alpaca.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.jacobpeterson.alpaca.properties.AlpacaProperties;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static net.jacobpeterson.alpaca.util.gson.GsonUtil.GSON;

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
     * Gets a new {@link okhttp3.HttpUrl.Builder}.
     *
     * @return a {@link okhttp3.HttpUrl.Builder}
     */
    public HttpUrl.Builder urlBuilder() {
        return baseURL.newBuilder();
    }

    /**
     * Gets a new {@link okhttp3.Request.Builder} with {@link #requestHeaders}.
     *
     * @param httpUrl the {@link okhttp3.HttpUrl}
     *
     * @return a {@link okhttp3.Request.Builder}
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
        try (Response response = request(request, isSuccessCode)) {
            try (ResponseBody responseBody = response.body()) {
                // Throw exception if 'responseBody' is ever null since we never want to use cachedResponse or
                // other types of responses.
                checkState(responseBody != null);

                try (Reader charStream = responseBody.charStream()) {
                    return JsonParser.parseReader(charStream);
                } catch (IOException ioException) {
                    throw new AlpacaClientException(ioException);
                }
            }
        }
    }

    /**
     * Sends a {@link Request} and ignores any {@link ResponseBody}.
     *
     * @param request       the {@link Request}
     * @param isSuccessCode throws {@link AlpacaClientException} if passed in {@link Response#code()} to the {@link
     *                      Predicate#test(Object)} returns false
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public void requestVoid(Request request, Predicate<Integer> isSuccessCode) throws AlpacaClientException {
        // close() will throw an exception if cachedResponse or others were used which is fine
        // since we never want to use those types of responses.
        request(request, isSuccessCode).close();
    }

    /**
     * Sends a {@link Request} and returns a {@link Response}.
     *
     * @param request       the {@link Request}
     * @param isSuccessCode throws {@link AlpacaClientException} if passed in {@link Response#code()} to the {@link
     *                      Predicate#test(Object)} returns false
     *
     * @return the {@link Response}. <strong>Be sure to call {@link Response#close()} after use.</strong>
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
        } catch (IOException ioException) {
            throw new AlpacaClientException(ioException);
        }
    }

    /**
     * Executes a {@link Request}.
     *
     * @param request the {@link Request}
     *
     * @return the {@link Response}. <strong>Be sure to call {@link Response#close()} after use.</strong>
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public Response executeRequest(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
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
