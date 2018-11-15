package io.github.mainstringargs.yahooFinance;

import java.util.List;
import io.github.mainstringargs.yahooFinance.domain.AssetProfile;
import io.github.mainstringargs.yahooFinance.domain.BalanceSheetHistory;
import io.github.mainstringargs.yahooFinance.domain.CalendarEvents;
import io.github.mainstringargs.yahooFinance.domain.CashflowStatementHistory;
import io.github.mainstringargs.yahooFinance.domain.DefaultKeyStatistics;
import io.github.mainstringargs.yahooFinance.domain.EarningsHistory;
import io.github.mainstringargs.yahooFinance.domain.EarningsTrend;
import io.github.mainstringargs.yahooFinance.domain.FinanceData;
import io.github.mainstringargs.yahooFinance.domain.FinancialData;
import io.github.mainstringargs.yahooFinance.domain.IncomeStatementHistory;
import io.github.mainstringargs.yahooFinance.domain.IndustryTrend;
import io.github.mainstringargs.yahooFinance.domain.RecommendationTrend;
import io.github.mainstringargs.yahooFinance.domain.Result;
import io.github.mainstringargs.yahooFinance.domain.UpgradeDowngradeHistory;


// TODO: Auto-generated Javadoc
/**
 * The Class YahooFinanceData.
 */
public class YahooFinanceData {

  /** The finance data. */
  private FinanceData financeData;
  
  /** The asset profile. */
  private AssetProfile assetProfile;
  
  /** The balance sheet history. */
  private BalanceSheetHistory balanceSheetHistory;
  
  /** The calendar of events. */
  private CalendarEvents calendarOfEvents;
  
  /** The cash flow statement history. */
  private CashflowStatementHistory cashFlowStatementHistory;
  
  /** The default key statistics. */
  private DefaultKeyStatistics defaultKeyStatistics;
  
  /** The earnings history. */
  private EarningsHistory earningsHistory;
  
  /** The earnings trend. */
  private EarningsTrend earningsTrend;
  
  /** The financial data. */
  private FinancialData financialData;
  
  /** The income statement history. */
  private IncomeStatementHistory incomeStatementHistory;
  
  /** The industry trend. */
  private IndustryTrend industryTrend;
  
  /** The recommendation trend. */
  private RecommendationTrend recommendationTrend;
  
  /** The upgrade downgrade history. */
  private UpgradeDowngradeHistory upgradeDowngradeHistory;

  /**
   * Instantiates a new yahoo finance data.
   *
   * @param financeData the finance data
   */
  public YahooFinanceData(FinanceData financeData) {
    this.financeData = financeData;

    if (financeData.getQuoteSummary() != null
        && financeData.getQuoteSummary().getResult() != null) {
      List<Result> results = financeData.getQuoteSummary().getResult();

      if (results != null & results.size() > 0) {
        Result result = results.get(0);

        if (result.getAssetProfile() != null) {
          assetProfile = result.getAssetProfile();
        }
        if (result.getBalanceSheetHistory() != null) {
          balanceSheetHistory = result.getBalanceSheetHistory();
        }
        if (result.getCalendarEvents() != null) {
          calendarOfEvents = result.getCalendarEvents();
        }
        if (result.getCashflowStatementHistory() != null) {
          cashFlowStatementHistory = result.getCashflowStatementHistory();
        }
        if (result.getDefaultKeyStatistics() != null) {
          defaultKeyStatistics = result.getDefaultKeyStatistics();
        }
        if (result.getEarningsHistory() != null) {
          earningsHistory = result.getEarningsHistory();
        }
        if (result.getEarningsTrend() != null) {
          earningsTrend = result.getEarningsTrend();
        }
        if (result.getFinancialData() != null) {
          financialData = result.getFinancialData();
        }
        if (result.getIncomeStatementHistory() != null) {
          incomeStatementHistory = result.getIncomeStatementHistory();
        }
        if (result.getIndustryTrend() != null) {
          industryTrend = result.getIndustryTrend();
        }
        if (result.getRecommendationTrend() != null) {
          recommendationTrend = result.getRecommendationTrend();
        }
        if (result.getUpgradeDowngradeHistory() != null) {
          upgradeDowngradeHistory = result.getUpgradeDowngradeHistory();
        }
      }
    }
  }

  /**
   * Gets the asset profile.
   *
   * @return the asset profile
   */
  public AssetProfile getAssetProfile() {
    return assetProfile;
  }

  /**
   * Gets the balance sheet history.
   *
   * @return the balance sheet history
   */
  public BalanceSheetHistory getBalanceSheetHistory() {
    return balanceSheetHistory;
  }

  /**
   * Gets the calendar of events.
   *
   * @return the calendar of events
   */
  public CalendarEvents getCalendarOfEvents() {
    return calendarOfEvents;
  }

  /**
   * Gets the cash flow statement history.
   *
   * @return the cash flow statement history
   */
  public CashflowStatementHistory getCashFlowStatementHistory() {
    return cashFlowStatementHistory;
  }

  /**
   * Gets the default key statistics.
   *
   * @return the default key statistics
   */
  public DefaultKeyStatistics getDefaultKeyStatistics() {
    return defaultKeyStatistics;
  }

  /**
   * Gets the earnings history.
   *
   * @return the earnings history
   */
  public EarningsHistory getEarningsHistory() {
    return earningsHistory;
  }

  /**
   * Gets the earnings trend.
   *
   * @return the earnings trend
   */
  public EarningsTrend getEarningsTrend() {
    return earningsTrend;
  }

  /**
   * Gets the financial data.
   *
   * @return the financial data
   */
  public FinancialData getFinancialData() {
    return financialData;
  }

  /**
   * Gets the income statement history.
   *
   * @return the income statement history
   */
  public IncomeStatementHistory getIncomeStatementHistory() {
    return incomeStatementHistory;
  }

  /**
   * Gets the industry trend.
   *
   * @return the industry trend
   */
  public IndustryTrend getIndustryTrend() {
    return industryTrend;
  }

  /**
   * Gets the recommendation trend.
   *
   * @return the recommendation trend
   */
  public RecommendationTrend getRecommendationTrend() {
    return recommendationTrend;
  }

  /**
   * Gets the upgrade downgrade history.
   *
   * @return the upgrade downgrade history
   */
  public UpgradeDowngradeHistory getUpgradeDowngradeHistory() {
    return upgradeDowngradeHistory;
  }

}
