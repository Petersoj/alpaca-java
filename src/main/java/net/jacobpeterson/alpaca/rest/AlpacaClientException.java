package net.jacobpeterson.alpaca.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.Nullable;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class AlpacaClientException extends Exception {

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";

    @Nullable
    private String responseBody;
    @Nullable
    private Integer responseStatusCode;
    @Nullable
    private String responseStatusMessage;
    @Nullable
    private Integer apiResponseCode;
    @Nullable
    private String apiResponseMessage;

    /**
     * Instantiates a new {@link AlpacaClientException}.
     *
     * @param message the message
     */
    public AlpacaClientException(String message) {
        super(message);
    }

    /**
     * Instantiates a new {@link AlpacaClientException}.
     *
     * @param cause the cause {@link Throwable}
     */
    public AlpacaClientException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new {@link AlpacaClientException}.
     *
     * @param message the message
     * @param cause   the cause {@link Throwable}
     */
    public AlpacaClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new {@link AlpacaClientException}.
     *
     * @param response the {@link Response} containing an API response code and message JSON
     */
    protected AlpacaClientException(Response response) {
        responseStatusCode = response.code();
        responseStatusMessage = response.message();

        try (ResponseBody clientResponseBody = response.body()) {
            if (clientResponseBody != null) {
                this.responseBody = clientResponseBody.string();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public String getMessage() {
        if (responseBody == null) {
            return super.getMessage();
        } else {
            return parseAPIErrorResponse();
        }
    }

    /**
     * Parses an API error response of the following format:
     * <pre>
     *  {
     *     "code": 505,
     *     "message": "Error message"
     *  }
     * </pre>
     * and sets {@link #apiResponseCode} and {@link #apiResponseMessage} accordingly.
     *
     * @return a formatted message of the error response
     */
    private String parseAPIErrorResponse() {
        try {
            JsonElement responseJsonElement = JsonParser.parseString(responseBody);

            if (responseJsonElement instanceof JsonObject) {
                JsonObject responseJsonObject = responseJsonElement.getAsJsonObject();

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
        } catch (Exception ignored) {}

        // Build message
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Status Code: ").append(responseStatusCode);
        messageBuilder.append(", Status Message: \"").append(responseStatusMessage).append("\"");

        if (apiResponseCode != null) {
            messageBuilder.append(", API Response Code: ").append(apiResponseCode);
        }

        if (apiResponseMessage != null) {
            messageBuilder.append(", API Response Message: \"").append(apiResponseMessage).append("\"");
        }

        return messageBuilder.toString();
    }

    @Nullable
    public Integer getResponseStatusCode() {
        return responseStatusCode;
    }

    @Nullable
    public String getResponseStatusMessage() {
        return responseStatusMessage;
    }

    @Nullable
    public Integer getAPIResponseCode() {
        return apiResponseCode;
    }

    @Nullable
    public String getAPIResponseMessage() {
        return apiResponseMessage;
    }
}
