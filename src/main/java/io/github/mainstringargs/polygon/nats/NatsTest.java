package io.github.mainstringargs.polygon.nats;

import java.nio.charset.StandardCharsets;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;

public class NatsTest {

  public static void main(String[] args) {

    String ticker = "QQQ";

    if (args.length == 1) {
      ticker = args[0];
    }

    Options o = new Options.Builder()
        .server("nats://" + AlpacaProperties.KEY_ID_VALUE + "@nats1.polygon.io:31101")
        .server("nats://" + AlpacaProperties.KEY_ID_VALUE + "@nats2.polygon.io:31102")
        .server("nats://" + AlpacaProperties.KEY_ID_VALUE + "@nats3.polygon.io:31103")
        .maxReconnects(-1).verbose().build();

    try {

      Connection nc = Nats.connect(o);

      Dispatcher d = nc.createDispatcher((msg) -> {
        String response = new String(msg.getData(), StandardCharsets.UTF_8);
        System.out.println(msg.getSubject() + " " + response);
      });


      d.subscribe("Q." + ticker); // quotes
      // d.subscribe("T.QQQ");
      // d.subscribe("A.QQQ");
      // d.subscribe("AM.QQQ");


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
