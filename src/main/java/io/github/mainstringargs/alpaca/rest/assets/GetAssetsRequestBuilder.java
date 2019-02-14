package io.github.mainstringargs.alpaca.rest.assets;

import io.github.mainstringargs.alpaca.enums.AssetStatus;

/**
 * The Class GetAssetsRequestBuilder.
 */
public class GetAssetsRequestBuilder extends AssetsRequestBuilder {


  /**
   * Instantiates a new gets the assets request builder.
   *
   * @param baseUrl the base url
   */
  public GetAssetsRequestBuilder(String baseUrl) {
    super(baseUrl);
  }


  /**
   * Status.
   *
   * @param status the status
   * @return the gets the assets request builder
   */
  public GetAssetsRequestBuilder status(AssetStatus status) {
    if (status != null) {
      super.appendURLParameter("status", status.getAPIName());
    }

    return this;
  }

  /**
   * Asset class.
   *
   * @param assetClass the asset class
   * @return the gets the assets request builder
   */
  public GetAssetsRequestBuilder assetClass(String assetClass) {
    if (assetClass != null) {
      super.appendURLParameter("asset_class", assetClass.trim());
    }

    return this;
  }

}
