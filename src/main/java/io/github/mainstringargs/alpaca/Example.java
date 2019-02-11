package io.github.mainstringargs.alpaca;

import java.util.List;
import io.github.mainstringargs.alpaca.domain.Account;
import io.github.mainstringargs.alpaca.domain.Calendar;
import io.github.mainstringargs.alpaca.domain.Clock;
import io.github.mainstringargs.alpaca.domain.Order;
import io.github.mainstringargs.alpaca.domain.Position;

/**
 * The Class Example.
 */
public class Example {

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {

    AlpacaAPI api = new AlpacaAPI();

    Clock clock = api.getClock();

    System.out.println(clock);

    Account account = api.getAccount();

    System.out.println(account);

    List<Position> positions = api.getPositions();

    for (Position position : positions) {
      System.out.println(position);
    }

    List<Order> orders = api.getOrders();

    for (Order order : orders) {
      System.out.println(order);
    }

    List<Calendar> calendar = api.getCalendar();

    for (Calendar cal : calendar) {
      System.out.println(cal);
    }

  }

}
