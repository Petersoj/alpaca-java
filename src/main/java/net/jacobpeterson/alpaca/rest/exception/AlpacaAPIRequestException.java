package net.jacobpeterson.alpaca.rest.exception;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.abstracts.rest.exception.AbstractAPIRequestException;
import net.jacobpeterson.alpaca.util.gson.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link AlpacaAPIRequestException} represents {@link HttpResponse} {@link Exception}s for {@link AlpacaAPI}.
 */
public class AlpacaAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new {@link AlpacaAPIRequestException}.
     *
     * @param cause the cause
     */
    public AlpacaAPIRequestException(Exception cause) {
        super(cause);
    }

    /**
     * Instantiates a new {@link AlpacaAPIRequestException}.
     *
     * @param httpResponse the {@link HttpResponse}
     */
    public AlpacaAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Alpaca", httpResponse);
    }

    /**
     * Parse a standard API exception response of the following format:
     * <pre>
     *  {
     *     "code": 40010001,
     *     "message": "Error message"
     *  }
     * </pre>
     */
    @Override
    protected void parseAPIExceptionMessage() {
        JsonElement responseJsonElement = JsonParser.parseString(new InputStreamReader(httpResponse.getBody()));

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
}
