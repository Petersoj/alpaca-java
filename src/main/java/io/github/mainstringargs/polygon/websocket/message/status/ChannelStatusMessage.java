package io.github.mainstringargs.polygon.websocket.message.status;

import com.google.gson.JsonObject;
import io.github.mainstringargs.domain.polygon.websocket.ChannelStatus;
import io.github.mainstringargs.polygon.enums.ChannelType;
import io.github.mainstringargs.polygon.websocket.message.ChannelMessage;
import io.github.mainstringargs.util.gson.GsonUtil;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The type Channel status message.
 */
public class ChannelStatusMessage implements ChannelMessage {

    /** The channel status. */
    private ChannelStatus channelStatus;

    /**
     * Instantiates a new Status message.
     *
     * @param jsonObject the json object
     */
    public ChannelStatusMessage(JsonObject jsonObject) {
        channelStatus = GsonUtil.GSON.fromJson(jsonObject, ChannelStatus.class);
    }

    @Override
    public String getTicker() {
        return null;
    }

    @Override
    public ChannelType getChannelType() {
        return null;
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

        ChannelStatusMessage that = (ChannelStatusMessage) o;

        return Objects.equals(this.channelStatus, that.channelStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelStatus);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("channelStatus = " + channelStatus)
                .toString();
    }
}
