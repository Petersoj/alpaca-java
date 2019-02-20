package io.github.mainstringargs.alpaca.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.alpaca.domain.Account;

/**
 * The Class AccountUpdateMessage.
 */
public class AccountUpdateMessage {

  /** The account. */
  private Account account;

  /**
   * Instantiates a new account update message.
   *
   * @param data the data
   */
  public AccountUpdateMessage(JsonObject data) {

    JsonObject jsonAccount = data.getAsJsonObject();

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setLenient();
    Gson gson = gsonBuilder.create();
    account = gson.fromJson(jsonAccount, Account.class);

  }

  /**
   * Gets the account.
   *
   * @return the account
   */
  public Account getAccount() {
    return account;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AccountUpdateMessage [account=" + account + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AccountUpdateMessage other = (AccountUpdateMessage) obj;
    if (account == null) {
      if (other.account != null)
        return false;
    } else if (!account.equals(other.account))
      return false;
    return true;
  }
}
