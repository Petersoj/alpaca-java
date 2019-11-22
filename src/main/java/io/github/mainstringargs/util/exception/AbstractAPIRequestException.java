package io.github.mainstringargs.util.exception;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The type Abstract API request exception.
 */
public abstract class AbstractAPIRequestException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The constant CODE_KEY. */
    protected static final String CODE_KEY = "code";

    /** The constant MESSAGE_KEY. */
    protected static final String MESSAGE_KEY = "message";

    /** The Api name. */
    protected final String apiName;

    /** The Http response. */
    protected HttpResponse<InputStream> httpResponse;

    /** The Request code. */
    protected int requestStatusCode;

    /** The Request status text. */
    protected String requestStatusText;

    /** The Api response code. */
    protected int apiResponseCode = -1;

    /** The Api response message. */
    protected String apiResponseMessage;

    /**
     * Instantiates a new Abstract api request exception.
     *
     * @param apiName      the api name
     * @param httpResponse the http response
     */
    public AbstractAPIRequestException(String apiName, HttpResponse<InputStream> httpResponse) {
        this.apiName = apiName;
        this.httpResponse = httpResponse;

        this.requestStatusCode = httpResponse.getStatus();
        this.requestStatusText = httpResponse.getStatusText();
    }

    /**
     * Parse the API exception message to populate the following fields:
     * <p>
     * {@link AbstractAPIRequestException#requestStatusCode}, {@link AbstractAPIRequestException#requestStatusText},
     * {@link AbstractAPIRequestException#apiResponseCode}, {@link AbstractAPIRequestException#apiResponseMessage}
     *
     * @param httpResponse the http response
     */
    protected abstract void parseAPIExceptionMessage(HttpResponse<InputStream> httpResponse);

    /**
     * Parse a standard API exception response.
     * <p>
     * e.g.
     * <p>
     * {
     * <p>
     * "code": 1001,
     * <p>
     * "message": "An error message"
     * <p>
     * }
     * <p>
     *
     * @param httpResponse the http response
     */
    protected void parseStandardAPIExceptionResponse(HttpResponse<InputStream> httpResponse) {
        JsonElement responseJsonElement = GsonUtil.JSON_PARSER.parse(new InputStreamReader(httpResponse.getBody()));

        if (responseJsonElement instanceof JsonObject) {
            JsonObject responseJsonObject = (JsonObject) responseJsonElement;

            if (responseJsonObject.has(CODE_KEY)) {
                apiResponseCode = responseJsonObject.get(CODE_KEY).getAsInt();
            }

            if (responseJsonObject.has(MESSAGE_KEY)) {
                apiResponseMessage = responseJsonObject.get(MESSAGE_KEY).getAsString();
            }
        }

        // Just use the response JSON if the message could not be parsed.
        if (apiResponseMessage == null) {
            apiResponseMessage = responseJsonElement.toString();
        }
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder(apiName).append(" API Request Exception! : ");
        messageBuilder.append("Status Code = ").append(requestStatusCode);
        messageBuilder.append(", Status Text = \"").append(requestStatusText).append("\"");

        if (apiResponseCode != -1) {
            messageBuilder.append(", API Response Code = ").append(apiResponseCode);
        }

        if (apiResponseMessage != null) {
            messageBuilder.append(", API Response Message = \"").append(apiResponseMessage).append("\"");
        }

        return messageBuilder.toString();
    }

    /**
     * Gets request status code.
     *
     * @return the request status code
     */
    public int getRequestStatusCode() {
        return requestStatusCode;
    }

    /**
     * Gets request status text.
     *
     * @return the request status text
     */
    public String getRequestStatusText() {
        return requestStatusText;
    }

    /**
     * Gets api response code.
     *
     * @return the api response code
     */
    public int getAPIResponseCode() {
        return apiResponseCode;
    }

    /**
     * Gets api response message.
     *
     * @return the api response message
     */
    public String getAPIResponseMessage() {
        return apiResponseMessage;
    }
}
