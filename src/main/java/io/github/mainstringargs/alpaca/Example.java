package io.github.mainstringargs.alpaca;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import io.github.mainstringargs.alpaca.domain.Account;
import io.github.mainstringargs.alpaca.domain.Calendar;
import io.github.mainstringargs.alpaca.domain.Clock;
import io.github.mainstringargs.alpaca.domain.Order;
import io.github.mainstringargs.alpaca.domain.Position;
import io.github.mainstringargs.alpaca.enums.BarsTimeFrame;
import io.github.mainstringargs.alpaca.enums.Direction;
import io.github.mainstringargs.alpaca.enums.OrderSide;
import io.github.mainstringargs.alpaca.enums.OrderStatus;
import io.github.mainstringargs.alpaca.enums.OrderTimeInForce;
import io.github.mainstringargs.alpaca.enums.OrderType;
import io.github.mainstringargs.alpaca.rest.exception.AlpacaAPIException;

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

    Clock clock = null;
    try {
      clock = api.getClock();
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }


    try {
      System.out.println(api.getBars(BarsTimeFrame.ONE_MIN, new String[] {"ZZ", "AMZN"}, 2, null,
          LocalDateTime.of(2019, Month.JANUARY, 6, 10, 10, 10), null, null));
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }

    try {
      Account account = api.getAccount();

      System.out.println(account);
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }

    try {
      List<Position> positions = api.getOpenPositions();

      for (Position position : positions) {
        System.out.println(position);
      }
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }


    try {
      LocalDateTime startDateTime = LocalDateTime.of(2019, Month.JANUARY, 5, 10, 10, 10);

      LocalDateTime endDateTime = LocalDateTime.of(2019, Month.JANUARY, 8, 13, 10, 10);

      List<Order> orders =
          api.getOrders(OrderStatus.ALL, 3, startDateTime, endDateTime, Direction.ASC);

      for (Order order : orders) {
        // System.out.println(order);
        System.out.println(api.getOrder(order.getId()));
      }
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }

    try {
      List<Calendar> calendar =
          api.getCalendar(LocalDate.of(2029, 11, 1), LocalDate.of(2029, 11, 7));

      for (Calendar cal : calendar) {
        System.out.println(cal);
      }
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }

    try {
      System.out.println(api.requestNewOrder("DIA", 3, OrderSide.SELL, OrderType.MARKET,
          OrderTimeInForce.DAY, null, null, null));
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }

    try {
      System.out.println(api.getOrderByClientId("MYCLIENTreID"));
    } catch (AlpacaAPIException e) {

      e.printStackTrace();
    }
  }

}
