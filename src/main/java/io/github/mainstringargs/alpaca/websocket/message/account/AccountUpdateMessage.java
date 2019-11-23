package io.github.mainstringargs.alpaca.websocket.message.account;

import com.google.gson.JsonObject;
import io.github.mainstringargs.alpaca.enums.StreamUpdateType;
import io.github.mainstringargs.alpaca.websocket.message.ChannelMessage;
import io.github.mainstringargs.domain.alpaca.websocket.AccountUpdate;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class AccountUpdateMessage.
 */
public class AccountUpdateMessage implements ChannelMessage {

    /** The account. */
    private AccountUpdate accountUpdate;

    /**
     * Instantiates a new account update message.
     *
     * @param accountUpdateJsonObject the account update json object
     */
    public AccountUpdateMessage(JsonObject accountUpdateJsonObject) {
        accountUpdate = GsonUtil.GSON.fromJson(accountUpdateJsonObject, AccountUpdate.class);
    }

    @Override
    public StreamUpdateType getMessageType() {
        return StreamUpdateType.ACCOUNT_UPDATES;
    }

    /**
     * Gets account update.
     *
     * @return the account update
     */
    public AccountUpdate getAccountUpdate() {
        return accountUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AccountUpdateMessage that = (AccountUpdateMessage) o;

        return Objects.equals(this.accountUpdate, that.accountUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountUpdate);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("accountUpdate = " + accountUpdate)
                .toString();
    }
}
