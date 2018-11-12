import io.github.mainstringargs.yahooFinance.YahooFinanceData;
import io.github.mainstringargs.yahooFinance.YahooFinanceModules;
import io.github.mainstringargs.yahooFinance.YahooFinanceRequest;
import io.github.mainstringargs.yahooFinance.YahooFinanceUrlBuilder;
import io.github.mainstringargs.yahooFinance.domain.FinancialData;

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
