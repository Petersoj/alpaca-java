package io.github.mainstringargs.alpaca.rest.exceptions;

import com.mashape.unirest.http.HttpResponse;
import io.github.mainstringargs.util.exception.AbstractAPIRequestException;

import java.io.InputStream;

/**
 * The Class AlpacaAPIException.
 */
public class AlpacaAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new Alpaca api exception.
     *
     * @param httpResponse the http response
     */
    public AlpacaAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Alpaca", httpResponse);
    }

    @Override
    protected void parseAPIExceptionMessage(HttpResponse<InputStream> httpResponse) {
        super.parseStandardAPIExceptionResponse(httpResponse);
    }
}
