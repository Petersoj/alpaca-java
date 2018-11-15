package io.github.mainstringargs.yahooFinance;

// TODO: Auto-generated Javadoc
/**
 * The Enum YahooFinanceModules.
 */
public enum YahooFinanceModules {

  /** The asset profile. */
  ASSET_PROFILE("assetProfile"), 
  
  /** The balance sheet history. */
  BALANCE_SHEET_HISTORY("balanceSheetHistory"), 
  
  /** The calendar events. */
  CALENDAR_EVENTS("calendarEvents"), 
  
  /** The cash flow statement history. */
  CASH_FLOW_STATEMENT_HISTORY("cashFlowStatementHistory"), 
  
  /** The default key statistics. */
  DEFAULT_KEY_STATISTICS("defaultKeyStatistics"), 
  
  /** The earnings history. */
  EARNINGS_HISTORY("earningsHistory"), 
  
  /** The earnings trend. */
  EARNINGS_TREND("earningsTrend"), 
  
  /** The financial data. */
  FINANCIAL_DATA("financialData"), 
  
  /** The income statement history. */
  INCOME_STATEMENT_HISTORY("incomeStatementHistory"), 
  
  /** The industry trend. */
  INDUSTRY_TREND("industryTrend"), 
  
  /** The recommendation trend. */
  RECOMMENDATION_TREND("recommendationTrend"), 
  
  /** The upgrade downgrade history. */
  UPGRADE_DOWNGRADE_HISTORY("upgradeDowngradeHistory");

  /** The module token. */
  private String moduleToken;

  /**
   * Instantiates a new yahoo finance modules.
   *
   * @param moduleToken the module token
   */
  YahooFinanceModules(String moduleToken) {
    this.moduleToken = moduleToken;
  }


  /**
   * Gets the module token.
   *
   * @return the module token
   */
  public String getModuleToken() {
    return moduleToken;
  }


}
