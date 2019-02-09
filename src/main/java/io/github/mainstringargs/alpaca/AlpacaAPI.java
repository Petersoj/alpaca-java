package io.github.mainstringargs.alpaca;

import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.domain.Account;
import io.github.mainstringargs.alpaca.domain.Order;
import io.github.mainstringargs.alpaca.domain.Position;
import io.github.mainstringargs.alpaca.properties.AlpacaProperties;
import io.github.mainstringargs.alpaca.rest.AccountUrlBuilder;
import io.github.mainstringargs.alpaca.rest.AlpacaRequest;
import io.github.mainstringargs.alpaca.rest.AlpacaUrlBuilder;
import io.github.mainstringargs.alpaca.rest.OrdersUrlBuilder;
import io.github.mainstringargs.alpaca.rest.PositionsUrlBuilder;

/**
 * The Class AlpacaAPI.
 */
public class AlpacaAPI {

  /** The key id. */
  private String keyId;

  /** The secret. */
  private String secret;

  /** The base url. */
  private String baseUrl;

  /** The alpaca request. */
  private AlpacaRequest alpacaRequest;

  /**
   * Instantiates a new alpaca API. Uses alpaca.properties for configuration.
   */
  public AlpacaAPI() {

    keyId = AlpacaProperties.KEY_ID_VALUE;
    secret = AlpacaProperties.SECRET_VALUE;
    baseUrl = AlpacaProperties.BASE_URL_VALUE;
    alpacaRequest = new AlpacaRequest(keyId, secret);

  }

  /**
   * Instantiates a new alpaca API.
   *
   * @param keyId the key id
   * @param secret the secret
   * @param baseUrl the base url
   */
  public AlpacaAPI(String keyId, String secret, String baseUrl) {
    this.keyId = keyId;
    this.secret = secret;
    this.baseUrl = baseUrl;
    alpacaRequest = new AlpacaRequest(keyId, secret);


  }

  /**
   * Gets the account.
   *
   * @return the account
   */
  public Account getAccount() {
    AlpacaUrlBuilder urlBuilder = new AccountUrlBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    Account account = alpacaRequest.getResponseObject(response, Account.class);

    return account;
  }

  /**
   * Gets the positions.
   *
   * @return the positions
   */
  public List<Position> getPositions() {
    Type listType = new TypeToken<List<Position>>() {}.getType();

    AlpacaUrlBuilder urlBuilder = new PositionsUrlBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Position> positions = alpacaRequest.getResponseObject(response, listType);

    return positions;
  }

  /**
   * Gets the orders.
   *
   * @return the orders
   */
  public List<Order> getOrders() {
    Type listType = new TypeToken<List<Order>>() {}.getType();

    AlpacaUrlBuilder urlBuilder = new OrdersUrlBuilder(baseUrl);

    HttpResponse<JsonNode> response = alpacaRequest.invokeGet(urlBuilder);

    List<Order> orders = alpacaRequest.getResponseObject(response, listType);

    return orders;
  }

}
