package io.github.mainstringargs.polygon.websocket;

import com.google.common.collect.Sets;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.properties.PolygonProperties;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PolygonWebsocketTestDriver {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        //  Configurator.setRootLevel(Level.ALL);

        PolygonWebsocketClient client = new PolygonWebsocketClient("AKKHS7AT2BYZNMLF30ZA",
                PolygonProperties.POLYGON_WEB_SOCKET_SERVER_URL_VALUE);

        Map<String, Set<ChannelType>> subscribedTypes = new HashMap<>();

        subscribedTypes.put("AAPL", Sets.newHashSet(ChannelType.values()));
        subscribedTypes.put("MSFT",
                Sets.newHashSet(ChannelType.AGGREGATE_PER_MINUTE, ChannelType.AGGREGATE_PER_SECOND));

        PolygonStreamListener listener1 = new PolygonStreamListenerAdapter(subscribedTypes) {
            @Override
            public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
                System.out.println(ticker + " " + channelType + " " + message);
            }
        };

        client.addListener(listener1);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        PolygonStreamListener listener2 = new PolygonStreamListenerAdapter(
                new HashSet<>(Arrays.asList("AAPL", "MSFT")), ChannelType.values()) {
            @Override
            public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
                System.out.println(ticker + " " + channelType + " " + message);
            }
        };

        client.addListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("remove one");
        client.removeListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("remove two");


        client.removeListener(listener2);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("remove three");


        client.removeListener(listener1);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("add one");

        client.addListener(listener2);

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}