package io.github.mainstringargs.alpaca.websocket;

import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class WebsocketTestDriver.
 */
public class AlpacaWebsocketTestDriver {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        AlpacaWebsocketClient client = new AlpacaWebsocketClient(AlpacaProperties.KEY_ID_VALUE,
                AlpacaProperties.SECRET_VALUE, AlpacaProperties.BASE_API_URL_VALUE);

        client.addListener(new AlpacaStreamListener() {
            @Override
            public Set<StreamUpdateType> getStreamUpdateTypes() {
                return new HashSet<StreamUpdateType>();
            }

            @Override
            public void streamUpdate(StreamUpdateType streamUpdateType, ChannelMessage message) {
                System.out.println(streamUpdateType + " " + message);
            }
        });

        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
