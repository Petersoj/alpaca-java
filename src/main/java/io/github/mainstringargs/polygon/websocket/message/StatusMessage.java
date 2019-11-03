package io.github.mainstringargs.polygon.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.ChannelStatus;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The Class StatusMessage.
 */
public class StatusMessage {

    /** The gson. */
    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gson = gsonBuilder.create();
    }

    /** The channel status. */
    private ChannelStatus channelStatus;

    /**
     * Instantiates a new Status message.
     *
     * @param jsonObject the json object
     */
    public StatusMessage(JsonObject jsonObject) {
        channelStatus = gson.fromJson(jsonObject, ChannelStatus.class);
    }

    /**
     * Gets channel status.
     *
     * @return the channel status
     */
    public ChannelStatus getChannelStatus() {
        return channelStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        StatusMessage that = (StatusMessage) o;

        return Objects.equals(this.channelStatus, that.channelStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelStatus, gson);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("channelStatus = " + channelStatus)
                .add("gson = " + gson)
                .toString();
    }
}
