package net.jacobpeterson.alpaca.rest.endpoint.corporateactions;

import com.google.gson.reflect.TypeToken;
import net.jacobpeterson.alpaca.model.endpoint.corporateactions.common.Announcement;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.AlpacaEndpoint;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link AlpacaEndpoint} for
 * <a href="https://docs.alpaca.markets/reference/get-v2-corporate_actions-announcements-id">Corporate actions</a>
 * Endpoint.
 */
public class CorporateActionsEndpoint extends AlpacaEndpoint {

    private static final Type ANNOUNCEMENT_ARRAYLIST_TYPE = new TypeToken<ArrayList<Announcement>>() {}.getType();

    private static final List<String> ALL_CA_TYPES = Arrays.asList("dividend", "merger", "spinoff", "split");

    /**
     * Instantiates a new {@link AlpacaEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public CorporateActionsEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "corporate_actions/announcements");
    }

    /**
     * Gets a {@link Announcement} of the requested id.
     * @param id - announcement id to query for
     * @return the {@link Announcement}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public Announcement getAnnouncement(String id) throws AlpacaClientException {
        checkNotNull(id);
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment)
                .addPathSegment(id);
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, Announcement.class);

    }

    /**
     * Gets list of all {@link Announcement} within last 90 days
     * @return a  {@link List} of {@link Announcement}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Announcement> getAnnouncements() throws AlpacaClientException {
        return this.getAnnouncements(ALL_CA_TYPES, LocalDate.now().minusDays(90), LocalDate.now(), null, null, null);
    }

    /**
     *  Gets list of all {@link Announcement} within last 90 days for specific symbol
     * @param symbol the symbol to query for
     * @return a  {@link List} of {@link Announcement}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Announcement> getAnnouncements(String symbol) throws AlpacaClientException {
        return this.getAnnouncements(ALL_CA_TYPES, LocalDate.now().minusDays(90), LocalDate.now(), symbol, null, null);
    }

    /**
     *  Gets list of all {@link Announcement} for specific query
     * @param caTypes - a list of Dividend, Merger, Spinoff, or Split
     * @param since - the start (inclusive) of the date range when searching corporate action announcements. This should follow the YYYY-MM-DD format. The date range is limited to 90 days
     * @param until - the end (inclusive) of the date range when searching corporate action announcements. This should follow the YYYY-MM-DD format. The date range is limited to 90 days
     * @param symbol - the symbol of the company initiating the announcement
     * @param cusip - the CUSIP of the company initiating the announcement
     * @param dateType - one of declaration_date, ex_date, record_date, or payable_date
     * @return a  {@link List} of {@link Announcement}
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<Announcement> getAnnouncements(
            List<String> caTypes, LocalDate since, LocalDate until,
            String symbol,
            String cusip,
            String dateType
            )
            throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegment(endpointPathSegment);
        checkNotNull(caTypes);
        checkNotNull(since);
        checkNotNull(until);


        urlBuilder.addQueryParameter("ca_types", String.join(",", caTypes));

        urlBuilder.addQueryParameter("since", since.toString());
        urlBuilder.addQueryParameter("until", until.toString());

        if (symbol != null) {
            urlBuilder.addQueryParameter("symbol", symbol);
        }
        if (cusip != null) {
            urlBuilder.addQueryParameter("cusip", cusip);
        }
        if (dateType !=null) {
            urlBuilder.addQueryParameter("date_type", dateType);
        }
        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();
        return alpacaClient.requestObject(request, ANNOUNCEMENT_ARRAYLIST_TYPE);

    }
}
