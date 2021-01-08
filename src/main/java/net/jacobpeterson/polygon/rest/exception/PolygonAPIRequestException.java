package net.jacobpeterson.polygon.rest.exception;

import com.mashape.unirest.http.HttpResponse;
import net.jacobpeterson.abstracts.rest.exception.AbstractAPIRequestException;

import java.io.InputStream;

/**
 * {@link PolygonAPIRequestException} represents {@link HttpResponse} request exceptions for {@link
 * net.jacobpeterson.polygon.PolygonAPI}.
 */
public class PolygonAPIRequestException extends AbstractAPIRequestException {

    /**
     * Instantiates a new {@link PolygonAPIRequestException}.
     *
     * @param httpResponse the {@link HttpResponse}
     */
    public PolygonAPIRequestException(HttpResponse<InputStream> httpResponse) {
        super("Polygon", httpResponse);
    }

    @Override
    protected void parseAPIExceptionMessage() {
        super.parseStandardAPIExceptionResponse();
    }
}
