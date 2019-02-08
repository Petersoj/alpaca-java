package io.github.mainstringargs.alpaca;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import io.github.mainstringargs.alpaca.domain.Account;

public class Example {

  public static void main(String[] args) {

    AlpacaUrlBuilder urlBuilder = new AlpacaUrlBuilder();
    urlBuilder.account();

    HttpResponse<JsonNode> response = AlpacaRequest.invokeGet(urlBuilder);

    Account account = AlpacaRequest.getResponseObject(response, Account.class);

    System.out.println(account);

  }

}
