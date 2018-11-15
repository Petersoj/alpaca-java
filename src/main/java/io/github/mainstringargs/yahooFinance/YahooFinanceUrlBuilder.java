package io.github.mainstringargs.yahooFinance;

import java.util.Arrays;
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooFinanceUrlBuilder.
 */
public class YahooFinanceUrlBuilder {


  /** The builder. */
  private StringBuilder builder;


  /** The base url. */
  private String baseUrl = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/";
  
  /** The formatted. */
  private boolean formatted = true;
  
  /** The crumb. */
  private String crumb = "swg7qs5y9UP";
  
  /** The lang. */
  private String lang = "en-US";
  
  /** The region. */
  private String region = "US";
  
  /** The symbol. */
  private String symbol = "AMZN";
  
  /** The module set. */
  private HashSet<YahooFinanceModules> moduleSet;
  
  /** The cors domain. */
  private String corsDomain = "finance.yahoo.com";


  /**
   * Instantiates a new yahoo finance url builder.
   */
  public YahooFinanceUrlBuilder() {
    builder = new StringBuilder(baseUrl);
  }

  /**
   * Base url.
   *
   * @param baseUrl the base url
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  /**
   * Formatted.
   *
   * @param formatted the formatted
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder formatted(boolean formatted) {
    this.formatted = formatted;
    return this;
  }

  /**
   * Crumb.
   *
   * @param crumb the crumb
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder crumb(String crumb) {
    this.crumb = crumb;
    return this;
  }

  /**
   * Lang.
   *
   * @param lang the lang
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder lang(String lang) {
    this.lang = lang;
    return this;
  }

  /**
   * Region.
   *
   * @param region the region
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder region(String region) {
    this.region = region;
    return this;
  }

  /**
   * Symbol.
   *
   * @param symbol the symbol
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Modules.
   *
   * @param financeModule the finance module
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder modules(YahooFinanceModules... financeModule) {
    this.moduleSet = new HashSet<YahooFinanceModules>(Arrays.asList(financeModule));
    return this;
  }

  /**
   * Cors domain.
   *
   * @param corsDomain the cors domain
   * @return the yahoo finance url builder
   */
  public YahooFinanceUrlBuilder corsDomain(String corsDomain) {
    this.corsDomain = corsDomain;
    return this;
  }

  /**
   * Gets the modules parameter.
   *
   * @return the modules parameter
   */
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

  /**
   * Gets the url.
   *
   * @return the url
   */
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

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "YahooFinanceUrlBuilder [baseUrl=" + baseUrl + ", builder=" + builder + ", formatted="
        + formatted + ", crumb=" + crumb + ", lang=" + lang + ", region=" + region + ", symbol="
        + symbol + ", moduleSet=" + moduleSet + ", corsDomain=" + corsDomain + "]";
  }


}
