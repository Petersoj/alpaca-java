package net.jacobpeterson.alpaca.abstracts.rest.exception;

import com.mashape.unirest.http.HttpResponse;

import java.io.InputStream;

/**
 * {@link AbstractAPIRequestException} represents {@link HttpResponse} request exceptions.
 */
public abstract class AbstractAPIRequestException extends Exception {

    protected static final String CODE_KEY = "code";
    protected static final String MESSAGE_KEY = "message";

    protected String apiName;
    protected HttpResponse<InputStream> httpResponse;
    protected Integer requestStatusCode;
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
     * {@inheritDoc}
     * <br>
     * This will call {@link #parseAPIExceptionMessage()} and return a formatted message.
     */
    @Override
    public String getMessage() {
        if (getCause() == null) {
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
        } else {
            return super.getMessage();
        }
    }

    /**
     * Gets request status code.
     *
     * @return the request status code
     */
    public Integer getRequestStatusCode() {
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
    public Integer getAPIResponseCode() {
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
