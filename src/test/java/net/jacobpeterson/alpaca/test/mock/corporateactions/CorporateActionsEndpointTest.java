package net.jacobpeterson.alpaca.test.mock.corporateactions;

import net.jacobpeterson.alpaca.model.endpoint.corporateactions.common.Announcement;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.corporateactions.CorporateActionsEndpoint;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CorporateActionsEndpointTest {

    @Test
    void getAnnouncements_givenSymbol_shouldReturnData() throws AlpacaClientException {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockMultipleAnnouncementsResponse());

        java.util.List<Announcement> result = newsEndpoint.getAnnouncements("K");

        assertNotNull(result);
        assertEquals(3, result.size());

    }

    @Test
    void getAnnouncements_shouldReturnData() throws AlpacaClientException {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockMultipleAnnouncementsResponse());

        java.util.List<Announcement> result = newsEndpoint.getAnnouncements();

        assertNotNull(result);
        assertEquals(3, result.size());

    }

    @Test
    void getAnnouncements_validation_caTypes() {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockMultipleAnnouncementsResponse());

        assertThrows(Exception.class,
                () -> newsEndpoint.getAnnouncements(null, LocalDate.now(), LocalDate.now(), null, null, null));
    }

    @Test
    void getAnnouncements_validation_since() {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockMultipleAnnouncementsResponse());

        assertThrows(Exception.class,
                () -> newsEndpoint.getAnnouncements(new ArrayList<>(), null, LocalDate.now(), null, null, null));
    }

    @Test
    void getAnnouncements_validation_until() {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockMultipleAnnouncementsResponse());

        assertThrows(Exception.class,
                () -> newsEndpoint.getAnnouncements(new ArrayList<>(), LocalDate.now(), null, null, null, null));
    }

    @Test
    void getAnnouncement_validation_id() {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockSingleAnnouncementResponse());

        assertThrows(Exception.class, () -> newsEndpoint.getAnnouncement(null));
    }

    @Test
    void getAnnouncement_shouldReturnData() throws AlpacaClientException {
        CorporateActionsEndpoint newsEndpoint = setupEndpoint(mockSingleAnnouncementResponse());

        Announcement announcement = newsEndpoint.getAnnouncement("1");

        assertNotNull(announcement);
        assertEquals("cd0cb768-889f-47a1-a94b-1438017dad77", announcement.getId());
    }

    private static CorporateActionsEndpoint setupEndpoint(String response) {
        MockInterceptor interceptor = new MockInterceptor();
        interceptor.addRule()
                .respond(200)
                .body(ResponseBody.create(response, MediaType.parse("application/json")));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        AlpacaClient api = new AlpacaClient(client, "token", "test", "/v1");

        return new CorporateActionsEndpoint(api);
    }

    private static String mockMultipleAnnouncementsResponse() {
        return "[{\"id\":\"cd0cb768-889f-47a1-a94b-1438017dad77\",\"corporate_action_id\":\"2642471\",\"ca_type\":\"spinoff\",\"ca_sub_type\":\"spinoff\",\"initiating_symbol\":\"KLG\",\"initiating_original_cusip\":\"92942W107\",\"target_symbol\":\"K\",\"target_original_cusip\":\"487836108\",\"effective_date\":\"2023-09-08\",\"ex_date\":\"2023-10-02\",\"record_date\":\"2023-09-21\",\"payable_date\":\"2023-10-02\",\"cash\":\"0\",\"old_rate\":\"1\",\"new_rate\":\"0.25\"},{\"id\":\"3a7fe233-7766-4f70-915f-9d1a5258eaa9\",\"corporate_action_id\":\"487836108_AD23\",\"ca_type\":\"dividend\",\"ca_sub_type\":\"cash\",\"initiating_symbol\":\"K\",\"initiating_original_cusip\":\"487836108\",\"target_symbol\":\"K\",\"target_original_cusip\":\"487836108\",\"declaration_date\":\"2023-09-19\",\"effective_date\":\"2023-09-21\",\"ex_date\":\"2023-10-02\",\"record_date\":\"2023-09-21\",\"payable_date\":\"2023-10-02\",\"cash\":\"0\",\"old_rate\":\"1\",\"new_rate\":\"1\"},{\"id\":\"ddf64bcb-fa76-4472-a98b-237f220ea38b\",\"corporate_action_id\":\"487836108_AE23\",\"ca_type\":\"dividend\",\"ca_sub_type\":\"cash\",\"initiating_symbol\":\"K\",\"initiating_original_cusip\":\"487836108\",\"target_symbol\":\"K\",\"target_original_cusip\":\"487836108\",\"declaration_date\":\"2023-11-29\",\"effective_date\":\"2023-12-01\",\"ex_date\":\"2023-11-30\",\"record_date\":\"2023-12-01\",\"payable_date\":\"2023-12-15\",\"cash\":\"0.56\",\"old_rate\":\"1\",\"new_rate\":\"1\"}]";
    }

    private static String mockSingleAnnouncementResponse() {
        return "{\"id\":\"cd0cb768-889f-47a1-a94b-1438017dad77\",\"corporate_action_id\":\"2642471\",\"ca_type\":\"spinoff\",\"ca_sub_type\":\"spinoff\",\"initiating_symbol\":\"KLG\",\"initiating_original_cusip\":\"92942W107\",\"target_symbol\":\"K\",\"target_original_cusip\":\"487836108\",\"effective_date\":\"2023-09-08\",\"ex_date\":\"2023-10-02\",\"record_date\":\"2023-09-21\",\"payable_date\":\"2023-10-02\",\"cash\":\"0\",\"old_rate\":\"1\",\"new_rate\":\"0.25\"}";
    }
}
