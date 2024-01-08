package net.jacobpeterson.alpaca.rest.endpoint.marketdata.news;

import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.news.NewsResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://docs.alpaca.markets/reference/news-1">News</a>
 * Endpoint.
 */
public class NewsEndpoint extends AlpacaEndpoint {

    /**
     * Instantiates a new {@link AlpacaEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public NewsEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "news");
    }

    /**
     * Gets latest list of news {@link NewsResponse}.
     * @return the {@link NewsResponse}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}
     */
    public NewsResponse getLatestNews() throws AlpacaClientException {
        return this.getLatestNews(null, null, null, null, null, null, null, null);
    }

    /**
     * Gets latest list of news {@link NewsResponse} for specific symbols.
     * @param symbols list of symbols to query news for
     * @return the {@link NewsResponse}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public NewsResponse getLatestNews(List<String> symbols) throws AlpacaClientException {
        checkNotNull(symbols);
        return this.getLatestNews(null, null, null, symbols, null, null, null, null);
    }

    /**
     * Gets latest list of news {@link NewsResponse} for specified token.
     * @param pageToken the ID of the end of your current page of results. (See the section on paging.)
     * @return the {@link NewsResponse}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public NewsResponse getLatestNewsPaged(String pageToken) throws AlpacaClientException {
        checkNotNull(pageToken);
        return this.getLatestNews(null, null, null, null, null, null, null, pageToken);
    }

    /**
     * Gets latest list of news {@link NewsResponse}
     * @param start The inclusive start of the interval
     * @param end the inclusive end of the interval
     * @param sortDirection sort articles by updated date
     * @param symbols list of symbols to query news for
     * @param limit Limit of news items to be returned for given page
     * @param includeContent Boolean indicator to include content for news articles (if available)
     * @param excludeContentless Boolean indicator to exclude news articles that do not contain content
     * @param pageToken the ID of the end of your current page of results. (See the section on paging.)
     * @return the {@link NewsResponse}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public NewsResponse getLatestNews(
            LocalDate start, LocalDate end,
            SortDirection sortDirection,
            List<String> symbols,
            Integer limit,
            Boolean includeContent,
            Boolean excludeContentless,
            String pageToken) throws AlpacaClientException {

        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);

        if (start != null) {
            urlBuilder.addQueryParameter("start", start.toString());
        }
        if (end != null) {
            urlBuilder.addQueryParameter("end", end.toString());
        }
        if (sortDirection != null) {
            urlBuilder.addQueryParameter("sort", sortDirection.value());
        }
        if (symbols != null && !symbols.isEmpty()) {
            urlBuilder.addQueryParameter("symbols", String.join(",", symbols));
        }
        if (limit != null) {
            urlBuilder.addQueryParameter("limit", Integer.toString(limit));
        }
        if (includeContent != null) {
            urlBuilder.addQueryParameter("include_content", Boolean.toString(includeContent));
        }
        if (excludeContentless != null) {
            urlBuilder.addQueryParameter("exclude_contentless", Boolean.toString(excludeContentless));
        }
        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();

        return alpacaClient.requestObject(request, NewsResponse.class);

    }
}
