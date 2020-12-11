package net.jacobpeterson.alpaca.rest.exception;

import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.rest.exception.AbstractAPIRequestException;

import java.io.InputStream;

/**
 * The Class {@link AlpacaAPIRequestException}.
 */
public class AlpacaAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new {@link AlpacaAPIRequestException}.
     *
     * @param httpResponse the http response
     */
    public AlpacaAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Alpaca", httpResponse);
    }

    @Override
    protected void parseAPIExceptionMessage() {
        super.parseStandardAPIExceptionResponse();
    }
}
