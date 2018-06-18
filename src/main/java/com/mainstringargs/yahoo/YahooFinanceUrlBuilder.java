package com.mainstringargs.yahoo;

import java.util.Arrays;
import java.util.HashSet;

public class YahooFinanceUrlBuilder {


  private StringBuilder builder;


  private String baseUrl = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/";
  private boolean formatted = true;
  private String crumb = "swg7qs5y9UP";
  private String lang = "en-US";
  private String region = "US";
  private String symbol = "AMZN";
  private HashSet<YahooFinanceModules> moduleSet;
  private String corsDomain = "finance.yahoo.com";


  public YahooFinanceUrlBuilder() {
    builder = new StringBuilder(baseUrl);
  }

  public YahooFinanceUrlBuilder baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  public YahooFinanceUrlBuilder formatted(boolean formatted) {
    this.formatted = formatted;
    return this;
  }

  public YahooFinanceUrlBuilder crumb(String crumb) {
    this.crumb = crumb;
    return this;
  }

  public YahooFinanceUrlBuilder lang(String lang) {
    this.lang = lang;
    return this;
  }

  public YahooFinanceUrlBuilder region(String region) {
    this.region = region;
    return this;
  }

  public YahooFinanceUrlBuilder symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  public YahooFinanceUrlBuilder modules(YahooFinanceModules... financeModule) {
    this.moduleSet = new HashSet<YahooFinanceModules>(Arrays.asList(financeModule));
    return this;
  }

  public YahooFinanceUrlBuilder corsDomain(String corsDomain) {
    this.corsDomain = corsDomain;
    return this;
  }

  private String getModulesParameter() {
    StringBuilder moduleParameter = new StringBuilder();

    if (moduleSet != null) {
      YahooFinanceModules[] modulesArray = moduleSet.toArray(new YahooFinanceModules[] {});

      for (int i = 0; i < modulesArray.length; i++) {
        YahooFinanceModules yahooFinanceModule = modulesArray[i];

        moduleParameter.append(yahooFinanceModule.getModuleToken());
        if (i < modulesArray.length - 1) {
          moduleParameter.append(",");
        }
      }
    }


    return moduleParameter.toString();
  }

  public String getURL() {


    builder.append(symbol);
    builder.append("?");
    builder.append("formatted");
    builder.append("=");
    builder.append(formatted);
    builder.append("&");
    builder.append("crumb");
    builder.append("=");
    builder.append(crumb);
    builder.append("&");
    builder.append("lang");
    builder.append("=");
    builder.append(lang);
    builder.append("&");
    builder.append("region");
    builder.append("=");
    builder.append(region);

    builder.append("&");
    builder.append("modules");
    builder.append("=");
    builder.append(getModulesParameter());

    builder.append("&");
    builder.append("corsDomain");
    builder.append("=");
    builder.append(corsDomain);


    return builder.toString();
  }

  @Override
  public String toString() {
    return "YahooFinanceUrlBuilder [baseUrl=" + baseUrl + ", builder=" + builder + ", formatted="
        + formatted + ", crumb=" + crumb + ", lang=" + lang + ", region=" + region + ", symbol="
        + symbol + ", moduleSet=" + moduleSet + ", corsDomain=" + corsDomain + "]";
  }


}
