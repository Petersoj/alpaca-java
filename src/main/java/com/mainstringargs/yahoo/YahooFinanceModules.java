package com.mainstringargs.yahoo;

public enum YahooFinanceModules {

  ASSET_PROFILE("assetProfile"), 
  BALANCE_SHEET_HISTORY("balanceSheetHistory"), 
  CALENDAR_EVENTS("calendarEvents"), 
  CASH_FLOW_STATEMENT_HISTORY("cashFlowStatementHistory"), 
  DEFAULT_KEY_STATISTICS("defaultKeyStatistics"), 
  EARNINGS_HISTORY("earningsHistory"), 
  EARNINGS_TREND("earningsTrend"), 
  FINANCIAL_DATA("financialData"), 
  INCOME_STATEMENT_HISTORY("incomeStatementHistory"), 
  INDUSTRY_TREND("industryTrend"), 
  RECOMMENDATION_TREND("recommendationTrend"), 
  UPGRADE_DOWNGRADE_HISTORY("upgradeDowngradeHistory");

  private String moduleToken;

  YahooFinanceModules(String moduleToken) {
    this.moduleToken = moduleToken;
  }


  public String getModuleToken() {
    return moduleToken;
  }


}
