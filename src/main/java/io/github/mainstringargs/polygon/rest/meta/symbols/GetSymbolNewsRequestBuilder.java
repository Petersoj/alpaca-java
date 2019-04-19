package io.github.mainstringargs.polygon.rest.meta.symbols;

/**
 * The Class GetSymbolNewsRequestBuilder.
 */
public class GetSymbolNewsRequestBuilder extends GetSymbolEndpointsRequestBuilder {


  /**
   * Instantiates a new gets the symbol news request builder.
   *
   * @param baseDataUrl the base data url
   */
  public GetSymbolNewsRequestBuilder(String baseDataUrl) {
    super(baseDataUrl);
  }


  /**
   * Perpage.
   *
   * @param perpage the perpage
   * @return the gets the symbol news request builder
   */
  public GetSymbolNewsRequestBuilder perpage(int perpage) {

    super.appendURLParameter("perpage", perpage + "");

    return this;
  }

  /**
   * Page.
   *
   * @param page the page
   * @return the gets the symbol news request builder
   */
  public GetSymbolNewsRequestBuilder page(int page) {
    super.appendURLParameter("page", page + "");

    return this;
  }


}
