package net.jacobpeterson.abstracts.rest.exception;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.util.gson.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link AbstractAPIRequestException} represents {@link HttpResponse} request exceptions.
 */
public abstract class AbstractAPIRequestException extends Exception {

    protected static final String CODE_KEY = "code";
    protected static final String MESSAGE_KEY = "message";

    protected final String apiName;
    protected HttpResponse<InputStream> httpResponse;
    protected int requestStatusCode;
    protected String requestStatusText;
    protected Integer apiResponseCode;
    protected String apiResponseMessage;

    /**
     * Instantiates a new {@link AbstractAPIRequestException}.
     *
     * @param apiName      the api name
     * @param httpResponse the http response
     */
    public AbstractAPIRequestException(String apiName, HttpResponse<InputStream> httpResponse) {
        super();

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
     */
    protected abstract void parseAPIExceptionMessage();

    /**
     * Parse a standard API exception response of the following format:
     * <pre>
     *  {
     *     "code": 40010001,
     *     "message": "Error message"
     *  }
     * </pre>
     */
    protected void parseStandardAPIExceptionResponse() {
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

    /**
     * {@inheritDoc} This will call {@link #parseAPIExceptionMessage()} and return a formatted message.
     */
    @Override
    public String getMessage() {
        this.parseAPIExceptionMessage();

        StringBuilder messageBuilder = new StringBuilder(apiName).append(" API Request Exception! : ");
        messageBuilder.append("Status Code = ").append(requestStatusCode);
        messageBuilder.append(", Status Text = \"").append(requestStatusText).append("\"");

        if (apiResponseCode != null) {
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
     * Gets API response code.
     *
     * @return the API response code
     */
    public int getAPIResponseCode() {
        return apiResponseCode;
    }

    /**
     * Gets API response message.
     *
     * @return the API response message
     */
    public String getAPIResponseMessage() {
        return apiResponseMessage;
    }
}
