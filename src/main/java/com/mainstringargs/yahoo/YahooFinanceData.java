package com.mainstringargs.yahoo;

import java.util.List;
import com.mainstringargs.yahoo.domain.AssetProfile;
import com.mainstringargs.yahoo.domain.BalanceSheetHistory;
import com.mainstringargs.yahoo.domain.CalendarEvents;
import com.mainstringargs.yahoo.domain.CashflowStatementHistory;
import com.mainstringargs.yahoo.domain.DefaultKeyStatistics;
import com.mainstringargs.yahoo.domain.EarningsHistory;
import com.mainstringargs.yahoo.domain.EarningsTrend;
import com.mainstringargs.yahoo.domain.FinanceData;
import com.mainstringargs.yahoo.domain.FinancialData;
import com.mainstringargs.yahoo.domain.IncomeStatementHistory;
import com.mainstringargs.yahoo.domain.IndustryTrend;
import com.mainstringargs.yahoo.domain.RecommendationTrend;
import com.mainstringargs.yahoo.domain.Result;
import com.mainstringargs.yahoo.domain.UpgradeDowngradeHistory;

public class YahooFinanceData {

  private FinanceData financeData;
  private AssetProfile assetProfile;
  private BalanceSheetHistory balanceSheetHistory;
  private CalendarEvents calendarOfEvents;
  private CashflowStatementHistory cashFlowStatementHistory;
  private DefaultKeyStatistics defaultKeyStatistics;
  private EarningsHistory earningsHistory;
  private EarningsTrend earningsTrend;
  private FinancialData financialData;
  private IncomeStatementHistory incomeStatementHistory;
  private IndustryTrend industryTrend;
  private RecommendationTrend recommendationTrend;
  private UpgradeDowngradeHistory upgradeDowngradeHistory;

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

  public AssetProfile getAssetProfile() {
    return assetProfile;
  }

  public BalanceSheetHistory getBalanceSheetHistory() {
    return balanceSheetHistory;
  }

  public CalendarEvents getCalendarOfEvents() {
    return calendarOfEvents;
  }

  public CashflowStatementHistory getCashFlowStatementHistory() {
    return cashFlowStatementHistory;
  }

  public DefaultKeyStatistics getDefaultKeyStatistics() {
    return defaultKeyStatistics;
  }

  public EarningsHistory getEarningsHistory() {
    return earningsHistory;
  }

  public EarningsTrend getEarningsTrend() {
    return earningsTrend;
  }

  public FinancialData getFinancialData() {
    return financialData;
  }

  public IncomeStatementHistory getIncomeStatementHistory() {
    return incomeStatementHistory;
  }

  public IndustryTrend getIndustryTrend() {
    return industryTrend;
  }

  public RecommendationTrend getRecommendationTrend() {
    return recommendationTrend;
  }

  public UpgradeDowngradeHistory getUpgradeDowngradeHistory() {
    return upgradeDowngradeHistory;
  }

}
