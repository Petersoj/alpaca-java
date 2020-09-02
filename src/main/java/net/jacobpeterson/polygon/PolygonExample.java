package net.jacobpeterson.polygon;

import net.jacobpeterson.domain.polygon.aggregates.Aggregate;
import net.jacobpeterson.domain.polygon.aggregates.AggregatesResponse;
import net.jacobpeterson.domain.polygon.stocksplits.StockSplit;
import net.jacobpeterson.domain.polygon.stocksplits.StockSplitsResponse;
import net.jacobpeterson.domain.polygon.websocket.PolygonStreamMessage;
import net.jacobpeterson.polygon.enums.Timespan;
import net.jacobpeterson.polygon.rest.exception.PolygonAPIRequestException;
import net.jacobpeterson.polygon.websocket.listener.PolygonStreamListenerAdapter;
import net.jacobpeterson.polygon.websocket.message.PolygonStreamMessageType;

import java.time.LocalDate;

/**
 * The Class Example.
 */
public class PolygonExample {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // This will use the key_id in the alpaca.properties file by default
        PolygonAPI polygonAPI = new PolygonAPI();

        String aaplTicker = "AAPL";

        // Add a Polygon stream listener to listen to "T.AAPL", "Q.AAPL", "A.AAPL", "AM.AAPL", and status messages
        polygonAPI.addPolygonStreamListener(new PolygonStreamListenerAdapter(aaplTicker,
                PolygonStreamMessageType.values()) {
            @Override
            public void onStreamUpdate(PolygonStreamMessageType streamMessageType, PolygonStreamMessage streamMessage) {
                System.out.println("===> streamUpdate " + streamMessageType + " " + streamMessage);
            }
        });

        // Sleep the current thread for 2 seconds so we can see some trade/quote/aggregates updates on the stream!
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            StockSplitsResponse stockSplitsResponse = polygonAPI.getStockSplits(aaplTicker);

            System.out.println(aaplTicker + " Stock Split Response:");
            System.out.println("\tStatus: " + stockSplitsResponse.getStatus());
            System.out.println("\tCount: " + stockSplitsResponse.getCount());
            for (StockSplit stockSplit : stockSplitsResponse.getResults()) {
                System.out.println("\t" + stockSplit.toString().replace(",", ",\n\t"));
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }

        try {
            AggregatesResponse aggregatesResponse = polygonAPI.getAggregates(aaplTicker, 1, Timespan.DAY,
                    LocalDate.of(2019, 11, 18), LocalDate.of(2019, 11, 25), false);

            System.out.println("Aggregate Response:");
            System.out.println("\tStatus: " + aggregatesResponse.getStatus());
            System.out.println("\tCount: " + aggregatesResponse.getResultsCount());
            for (Aggregate aggregate : aggregatesResponse.getResults()) {
                System.out.println("\t" + aggregate.toString().replace(",", ",\n\t"));
            }
        } catch (PolygonAPIRequestException e) {
            e.printStackTrace();
        }
    }
}
