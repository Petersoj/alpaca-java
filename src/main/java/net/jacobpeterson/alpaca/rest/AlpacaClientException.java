package net.jacobpeterson.alpaca.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.Reader;

import static com.google.common.base.Preconditions.checkState;

public class AlpacaClientException extends Exception {

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";

    private Response response;
    private Integer requestStatusCode;
    private String requestStatusMessage;
    private Integer apiResponseCode;
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
        this.response = response;
    }

    @Override
    public String getMessage() {
        if (response == null) {
            return super.getMessage();
        } else {
            requestStatusCode = response.code();
            requestStatusMessage = response.message();

            try {
                return parseAPIErrorResponse();
            } catch (Exception ignored) {
                return super.getMessage();
            }
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
     *
     * @throws IOException thrown for {@link IOException}s
     */
    private String parseAPIErrorResponse() throws IOException {
        ResponseBody responseBody = response.body();
        checkState(responseBody != null);

        try (Reader charStream = responseBody.charStream()) {
            JsonElement responseJsonElement = JsonParser.parseReader(charStream);

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

            // Build message
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Status Code: ").append(requestStatusCode);
            messageBuilder.append(", Status Message: \"").append(requestStatusMessage).append("\"");

            if (apiResponseCode != null) {
                messageBuilder.append(", API Response Code: ").append(apiResponseCode);
            }

            if (apiResponseMessage != null) {
                messageBuilder.append(", API Response Message: \"").append(apiResponseMessage).append("\"");
            }

            return messageBuilder.toString();
        }
    }

    /**
     * Gets the {@link #requestStatusCode}.
     *
     * @return an {@link Integer}
     */
    public Integer getRequestStatusCode() {
        return requestStatusCode;
    }

    /**
     * Gets the {@link #requestStatusMessage}.
     *
     * @return a {@link String}
     */
    public String getRequestStatusMessage() {
        return requestStatusMessage;
    }

    /**
     * Gets the {@link #apiResponseCode}.
     *
     * @return an {@link Integer}
     */
    public Integer getAPIResponseCode() {
        return apiResponseCode;
    }

    /**
     * Gets the {@link #apiResponseMessage}.
     *
     * @return a {@link String}
     */
    public String getAPIResponseMessage() {
        return apiResponseMessage;
    }

    @Override
    public String toString() {
        return "AlpacaClientException{" +
                "response=" + response +
                ", requestStatusCode=" + requestStatusCode +
                ", requestStatusMessage='" + requestStatusMessage + '\'' +
                ", apiResponseCode=" + apiResponseCode +
                ", apiResponseMessage='" + apiResponseMessage + '\'' +
                "}";
    }
}
