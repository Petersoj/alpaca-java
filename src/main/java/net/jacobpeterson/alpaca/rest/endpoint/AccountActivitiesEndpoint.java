package net.jacobpeterson.alpaca.rest.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.AccountActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.NonTradeActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.TradeActivity;
import net.jacobpeterson.alpaca.model.endpoint.accountactivities.enums.ActivityType;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static net.jacobpeterson.alpaca.util.gson.GsonUtil.GSON;

/**
 * {@link AbstractEndpoint} for
 * <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">Account
 * Activities</a>.
 */
public class AccountActivitiesEndpoint extends AbstractEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountActivitiesEndpoint.class);

    private static final String ACTIVITY_TYPE_FIELD = "activity_type";

    /**
     * Instantiates a new {@link AccountActivitiesEndpoint}.
     *
     * @param alpacaClient the {@link AlpacaClient}
     */
    public AccountActivitiesEndpoint(AlpacaClient alpacaClient) {
        super(alpacaClient, "account/activities");
    }

    /**
     * Returns {@link AccountActivity} entries for many {@link ActivityType}s or for a specific {@link ActivityType}.
     *
     * @param date          the date for which you want to see activities.
     * @param until         the response will contain only activities submitted before this date. (Cannot be used with
     *                      <code>date</code>.)
     * @param after         the response will contain only activities submitted after this date. (Cannot be used with
     *                      <code>date</code>.)
     * @param sortDirection the {@link SortDirection} (defaults to {@link SortDirection#DESCENDING} if unspecified.)
     * @param pageSize      the maximum number of entries to return in the response. (See the section on paging.)
     * @param pageToken     the ID of the end of your current page of results. (See the section on paging.)
     * @param activityTypes the {@link ActivityType}s (null for all {@link ActivityType}s)
     *
     * @return a {@link List} of {@link AccountActivity}s
     *
     * @throws AlpacaClientException thrown for {@link AlpacaClientException}s
     */
    public List<AccountActivity> get(ZonedDateTime date, ZonedDateTime until, ZonedDateTime after,
            SortDirection sortDirection, Integer pageSize, String pageToken, ActivityType... activityTypes)
            throws AlpacaClientException {
        HttpUrl.Builder urlBuilder = alpacaClient.urlBuilder()
                .addPathSegments(endpointPathSegment);

        if (activityTypes != null) { // Check if we don't want all activity types
            if (activityTypes.length == 1) { // Get one activity
                urlBuilder.addPathSegment(activityTypes[0].toString());
            } else { // Get list of activities
                urlBuilder.addQueryParameter("activity_types",
                        // Makes comma-separated list
                        Arrays.stream(activityTypes).map(ActivityType::toString).collect(Collectors.joining(",")));
            }
        }

        if (date != null) {
            urlBuilder.addQueryParameter("date", date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (until != null) {
            urlBuilder.addQueryParameter("until", until.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (after != null) {
            urlBuilder.addQueryParameter("after", after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        }

        if (sortDirection != null) {
            urlBuilder.addQueryParameter("direction", sortDirection.toString());
        }

        if (pageSize != null) {
            urlBuilder.addQueryParameter("page_size", pageSize.toString());
        }

        if (pageToken != null) {
            urlBuilder.addQueryParameter("page_token", pageToken);
        }

        Request request = alpacaClient.requestBuilder(urlBuilder.build())
                .get()
                .build();

        JsonElement responseJSON = alpacaClient.requestJSON(request);
        checkState(responseJSON instanceof JsonArray, "The response must be an array! Received: %s", responseJSON);

        ArrayList<AccountActivity> accountActivities = new ArrayList<>();
        for (JsonElement responseArrayElement : responseJSON.getAsJsonArray()) {
            checkState(responseArrayElement instanceof JsonObject,
                    "All array elements must be objects! Received: %s", responseArrayElement);

            JsonObject responseArrayObject = responseArrayElement.getAsJsonObject();
            JsonElement activityTypeElement = responseArrayObject.get(ACTIVITY_TYPE_FIELD);
            checkState(activityTypeElement != null, "Activity type elements must have %s field! Received: %s",
                    ACTIVITY_TYPE_FIELD, responseArrayElement);

            String activityType = activityTypeElement.getAsString();
            AccountActivity accountActivity;

            // A 'TradeActivity' always has 'activity_type' field as 'FILL'
            if (activityType.equals(ActivityType.FILL.toString())) {
                accountActivity = GSON.fromJson(responseArrayObject, TradeActivity.class);
            } else {
                accountActivity = GSON.fromJson(responseArrayObject, NonTradeActivity.class);
            }

            if (accountActivity.getActivityType() == null) {
                LOGGER.error("No ActivityType enum exists for {}. Report this!", activityType);
            }

            accountActivities.add(accountActivity);
        }

        return accountActivities;
    }
}
