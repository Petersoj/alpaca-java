package io.github.mainstringargs.polygon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Sets;
import io.github.mainstringargs.polygon.domain.SymbolAnalystRatings;
import io.github.mainstringargs.polygon.domain.SymbolDetails;
import io.github.mainstringargs.polygon.domain.SymbolDividend;
import io.github.mainstringargs.polygon.domain.SymbolEarning;
import io.github.mainstringargs.polygon.domain.SymbolEndpoints;
import io.github.mainstringargs.polygon.domain.SymbolFinancial;
import io.github.mainstringargs.polygon.domain.SymbolNews;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.nats.PolygonStreamListener;
import io.github.mainstringargs.polygon.nats.message.ChannelMessage;
import io.github.mainstringargs.polygon.rest.exceptions.PolygonAPIException;

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
    PolygonAPI polygonAPI = new PolygonAPI();

    String ticker = "MSFT";


    try {
      SymbolEndpoints symbolEndPoints = polygonAPI.getSymbolEndpoints(ticker);

      System.out.println("\n\n" + ticker + " Symbol Endpoints:");
      System.out.println("\t" + symbolEndPoints.getSymbol().getSymbol() + " "
          + symbolEndPoints.getSymbol().getName() + " " + symbolEndPoints.getSymbol().getType()
          + symbolEndPoints.getSymbol().getUrl() + " " + symbolEndPoints.getSymbol().getUpdated());
      System.out.println("\t" + symbolEndPoints.getEndpoints());



    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }

    try {
      SymbolDetails symbolDetails = polygonAPI.getSymbolDetails(ticker);

      System.out.println("\n\n" + ticker + " Symbol Details:");
      System.out.println("\t" + symbolDetails);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }

    try {
      SymbolAnalystRatings symbolAnalystRatings = polygonAPI.getSymbolAnalystRatings(ticker);

      System.out.println("\n\n" + ticker + " Symbol Analyst Ratings:");
      System.out.println("\t" + symbolAnalystRatings);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }

    try {
      List<SymbolDividend> symbolDividends = polygonAPI.getSymbolDividends(ticker);

      System.out.println("\n\n" + ticker + " Symbol Dividends:");

      for (SymbolDividend div : symbolDividends)
        System.out.println("\t" + div);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }


    try {
      List<SymbolEarning> symbolEarnings = polygonAPI.getSymbolEarnings(ticker);

      System.out.println("\n\n" + ticker + " Symbol Earnings:");

      for (SymbolEarning earning : symbolEarnings)
        System.out.println("\t" + earning);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }


    try {
      List<SymbolFinancial> symbolFinancials = polygonAPI.getSymbolFinancials(ticker);

      System.out.println("\n\n" + ticker + " Symbol Financials:");
      for (SymbolFinancial financial : symbolFinancials)
        System.out.println("\t" + financial);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }


    try {
      List<SymbolNews> symbolNews = polygonAPI.getSymbolNews(ticker);

      System.out.println("\n\n" + ticker + " Symbol News:");
      for (SymbolNews newsItem : symbolNews)
        System.out.println("\t" + newsItem);


    } catch (PolygonAPIException e) {
      e.printStackTrace();
    }

    polygonAPI.addPolygonStreamListener(new PolygonStreamListener() {

      @Override
      public void streamUpdate(String ticker, ChannelType channelType, ChannelMessage message) {
        System.out.println("===> streamUpdate " + ticker + " " + channelType + " " + message);

      }

      @Override
      public Map<String, Set<ChannelType>> getStockChannelTypes() {
        Map<String, Set<ChannelType>> subscribedTypes = new HashMap<>();
        subscribedTypes.put(ticker, Sets.newHashSet(ChannelType.values()));
        return subscribedTypes;
      }
    });

    try {
      Thread.sleep(10000L);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.exit(0);

  }

}
