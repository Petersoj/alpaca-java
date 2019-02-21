package io.github.mainstringargs.alpaca.websocket;

import java.util.HashSet;
import java.util.Set;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

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
        AlpacaProperties.SECRET_VALUE, AlpacaProperties.BASE_ACCOUNT_URL_VALUE);

    client.addObserver(new AlpacaStreamListener() {

      @Override
      public Set<MessageType> getMessageTypes() {
        return new HashSet<MessageType>();
      }

      @Override
      public void streamUpdate(MessageType messageType, Object message) {
        System.out.println(messageType + " " + message);

      }
    });



    try {
      Thread.sleep(1000000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
