package net.jacobpeterson.alpaca.rest.exception;

import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.rest.exception.AbstractAPIRequestException;

import java.io.InputStream;

/**
 * {@link AlpacaAPIRequestException} represents {@link HttpResponse} request exceptions for {@link
 * net.jacobpeterson.alpaca.AlpacaAPI}.
 */
public class AlpacaAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new {@link AlpacaAPIRequestException}.
     *
     * @param httpResponse the {@link HttpResponse}
     */
    public AlpacaAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Alpaca", httpResponse);
    }

    @Override
    protected void parseAPIExceptionMessage() {
        super.parseStandardAPIExceptionResponse();
    }
}
