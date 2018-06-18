import com.mainstringargs.yahoo.YahooFinanceData;
import com.mainstringargs.yahoo.YahooFinanceModules;
import com.mainstringargs.yahoo.YahooFinanceRequest;
import com.mainstringargs.yahoo.YahooFinanceUrlBuilder;
import com.mainstringargs.yahoo.domain.FinancialData;

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


      if (financeData.getFinancialData() != null) {
        FinancialData financials = financeData.getFinancialData();

        System.out.println(symbol + ": currentPrice: $" + financials.getCurrentPrice().getRaw()
            + "; recommendationMean " + financials.getRecommendationMean().getRaw());
      }
    }
  }
}
