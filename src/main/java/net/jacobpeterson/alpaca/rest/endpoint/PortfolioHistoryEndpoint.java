package net.jacobpeterson.alpaca.rest.endpoint;

import net.jacobpeterson.alpaca.model.endpoint.portfoliohistory.PortfolioHistory;
import net.jacobpeterson.alpaca.model.endpoint.portfoliohistory.PortfolioHistoryDataPoint;
import net.jacobpeterson.alpaca.model.endpoint.portfoliohistory.PortfolioHistoryResponse;
import net.jacobpeterson.alpaca.model.endpoint.portfoliohistory.enums.PortfolioPeriodUnit;
import net.jacobpeterson.alpaca.model.endpoint.portfoliohistory.enums.PortfolioTimeFrame;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.util.format.FormatUtil;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://alpaca.markets/docs/api-documentation/api-v2/portfolio-history/">Portfolio
 * History</a>.
 */
public class PortfolioHistoryEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link PortfolioHistoryEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public PortfolioHistoryEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "account/portfolio/history");
    }

    /**
     * Returns timeseries data about equity and profit/loss (P/L) of the account in requested timespan.
     *
     * @param periodLength  the duration of the <code>periodUnit</code>. Defaults to <code>1</code>.
     * @param periodUnit    the {@link PortfolioPeriodUnit}. Defaults to {@link PortfolioPeriodUnit#MONTH}.
     * @param timeFrame     the resolution of time window. If omitted, {@link PortfolioTimeFrame#ONE_MIN} for less than
     *                      7 days period, {@link PortfolioTimeFrame#FIFTEEN_MINUTE} for less than 30 days, or otherwise
     *                      {@link PortfolioTimeFrame#ONE_DAY}.
     * @param dateEnd       the date the data is returned up to. Defaults to the current market date (rolls over at the
     *                      market open if <code>extended_hours</code> is false, otherwise at 7 AM ET)
     * @param extendedHours if true, include extended hours in the result. This is effective only for timeframe less
     *                      than {@link PortfolioTimeFrame#ONE_DAY}.
     *
     * @return the {@link PortfolioHistory}
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public PortfolioHistory get(Integer periodLength, PortfolioPeriodUnit periodUnit, PortfolioTimeFrame timeFrame,
            LocalDate dateEnd, Boolean extendedHours) throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegments(endpointPathSegment);

        if (periodLength != null && periodUnit != null) {
            urlBuilder.addQueryParameter("period", periodLength + periodUnit.toString());
        }

        if (timeFrame != null) {
            urlBuilder.addQueryParameter("timeframe", timeFrame.toString());
        }

        if (dateEnd != null) {
            urlBuilder.addQueryParameter("date_end", dateEnd.format(DateTimeFormatter.ISO_DATE));
        }

        if (extendedHours != null) {
            urlBuilder.addQueryParameter("extended_hours", extendedHours.toString());
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        PortfolioHistoryResponse response = alpacaClient.requestObject(request,
                PortfolioHistoryResponse.class);

        // Check if any response arrays differ in size
        checkState(response.getTimestamp().size() == response.getEquity().size() &&
                        response.getEquity().size() == response.getProfitLoss().size() &&
                        response.getProfitLoss().size() == response.getProfitLossPct().size(),
                "Response arrays should not differ in size!");

        // Add all data points into one POJO
        ArrayList<PortfolioHistoryDataPoint> dataPoints = new ArrayList<>();
        for (int index = 0; index < response.getTimestamp().size(); index++) {
            dataPoints.add(new PortfolioHistoryDataPoint(
                    Instant.ofEpochSecond(response.getTimestamp().get(index)).atZone(FormatUtil.NEW_YORK_ZONED_ID),
                    response.getEquity().get(index),
                    response.getProfitLoss().get(index),
                    response.getProfitLossPct().get(index)));
        }

        return new PortfolioHistory(
                dataPoints,
                response.getBaseValue(),
                response.getTimeframe());
    }
}
