import com.mainstringargs.yahoo.YahooFinanceData;
import com.mainstringargs.yahoo.YahooFinanceModules;
import com.mainstringargs.yahoo.YahooFinanceRequest;
import com.mainstringargs.yahoo.YahooFinanceUrlBuilder;

public class Example {

  public static void main(String[] args) {

    if (args.length == 0) {
      args = new String[] {"fb", "amzn", "baba"};
    }

    for (String symbol : args) {

      YahooFinanceUrlBuilder builder =
          new YahooFinanceUrlBuilder().modules(YahooFinanceModules.values()).symbol(symbol);

      YahooFinanceRequest request = new YahooFinanceRequest();

      YahooFinanceData financeData = request.getFinanceData(request.invoke(builder));


      if (financeData.getFinancialData() != null)
        System.out.println(symbol + ": recMean "
            + financeData.getFinancialData().getRecommendationMean().getRaw());
    }
  }
}
