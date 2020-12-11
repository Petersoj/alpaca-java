package net.jacobpeterson.polygon.rest.exception;

import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.rest.exception.AbstractAPIRequestException;

import java.io.InputStream;

/**
 * The Class {@link PolygonAPIRequestException}.
 */
public class PolygonAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new {@link PolygonAPIRequestException}.
     *
     * @param httpResponse the http response
     */
    public PolygonAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Polygon", httpResponse);
    }

    @Override
    protected void parseAPIExceptionMessage() {
        super.parseStandardAPIExceptionResponse();
    }
}
