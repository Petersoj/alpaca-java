package net.jacobpeterson.alpaca.test.mock.marketdata.news;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.news.NewsResponse;
import net.jacobpeterson.alpaca.rest.AlpacaClient;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.marketdata.news.NewsEndpoint;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.mock.MockInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsEndpointTest {

    @Test
    void getLatestNews_validation_page() {
        NewsEndpoint newsEndpoint = setupEndpoint(mockResponse());

        assertThrows(Exception.class, () -> newsEndpoint.getLatestNewsPaged(null));
    }

    @Test
    void getLatestNews_validation_symbols() {
        NewsEndpoint newsEndpoint = setupEndpoint(mockResponse());

        assertThrows(Exception.class, () -> newsEndpoint.getLatestNews(null));
    }

    @Test
    void getLatestNews_shouldReturnData() throws AlpacaClientException {
        NewsEndpoint newsEndpoint = setupEndpoint(mockResponse());
        NewsResponse response = newsEndpoint.getLatestNews(null, null, null, null, null, null, null, null);

        assertNotNull(response);
        assertEquals("MTcwNDczNjg2NTAwMDAwMDAwMHwzNjU0MDM2Mw==", response.getNextPageToken());
        assertEquals(1, response.getNews().size());
        assertEquals(3, response.getNews().get(0).getImages().size());

    }

    @Test
    void getLatestNews_shouldReturnLatestData() throws AlpacaClientException {
        NewsEndpoint newsEndpoint = setupEndpoint(mockResponse());
        NewsResponse response = newsEndpoint.getLatestNews();

        assertNotNull(response);
        assertEquals("MTcwNDczNjg2NTAwMDAwMDAwMHwzNjU0MDM2Mw==", response.getNextPageToken());
        assertEquals(1, response.getNews().size());
        assertEquals(3, response.getNews().get(0).getImages().size());

    }

    private static NewsEndpoint setupEndpoint(String response) {
        MockInterceptor interceptor = new MockInterceptor();
        interceptor.addRule()
                .respond(200)
                .body(ResponseBody.create(response, MediaType.parse("application/json")));

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        AlpacaClient api = new AlpacaClient(client, "token", "test", "/v1");

        return new NewsEndpoint(api);
    }

    private static String mockResponse() {
        return "{\n" +
                "  \"news\": [\n" +
                "    {\n" +
                "      \"author\": \"Khyathi Dalal\",\n" +
                "      \"content\": \"\",\n" +
                "      \"created_at\": \"2024-01-08T18:07:34Z\",\n" +
                "      \"headline\": \"Solana Drops 9.9%, Solana Memecoins Tank Over 20%: Is This The End Of The Dogecoin Killers?\",\n" +
                "      \"id\": 36536785,\n" +
                "      \"images\": [\n" +
                "        {\n" +
                "          \"size\": \"large\",\n" +
                "          \"url\": \"https://cdn.benzinga.com/files/imagecache/2048x1536xUP/images/story/2024/01/08/solana_coin_shutter2_0.jpg\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"size\": \"small\",\n" +
                "          \"url\": \"https://cdn.benzinga.com/files/imagecache/1024x768xUP/images/story/2024/01/08/solana_coin_shutter2_0.jpg\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"size\": \"thumb\",\n" +
                "          \"url\": \"https://cdn.benzinga.com/files/imagecache/250x187xUP/images/story/2024/01/08/solana_coin_shutter2_0.jpg\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"source\": \"benzinga\",\n" +
                "      \"summary\": \"Solana (CRYPTO: SOL) dropped 1.4% in the past 24 hours, extending its losses over the previous seven days to 9.9%. Solana-based memecoins are particularly hard-hit by the correction, taking much heavier losses than established memecoins.\",\n" +
                "      \"symbols\": [\n" +
                "        \"BONKUSD\",\n" +
                "        \"DOGEUSD\",\n" +
                "        \"ETHUSD\",\n" +
                "        \"SHIBUSD\",\n" +
                "        \"SOLUSD\"\n" +
                "      ],\n" +
                "      \"updated_at\": \"2024-01-08T18:07:35Z\",\n" +
                "      \"url\": \"https://www.benzinga.com/markets/cryptocurrency/24/01/36536785/solana-drops-9-9-solana-memecoins-tank-over-20-is-this-the-end-of-the-dogecoin-killers\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"next_page_token\": \"MTcwNDczNjg2NTAwMDAwMDAwMHwzNjU0MDM2Mw==\"\n" +
                "}";
    }
}
