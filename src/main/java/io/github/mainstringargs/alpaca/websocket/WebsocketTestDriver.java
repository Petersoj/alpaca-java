package io.github.mainstringargs.alpaca.websocket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;

/**
 * The Class WebsocketTestDriver.
 */
public class WebsocketTestDriver {

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {

    WebsocketClient client = new WebsocketClient(AlpacaProperties.KEY_ID_VALUE,
        AlpacaProperties.SECRET_VALUE, AlpacaProperties.BASE_ACCOUNT_URL_VALUE);

    client.addObserver(new WebsocketObserver() {

      @Override
      public Set<MessageType> getMessageTypes() {
        return new HashSet<MessageType>(Arrays.asList(MessageType.values()));
      }
    });



    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
