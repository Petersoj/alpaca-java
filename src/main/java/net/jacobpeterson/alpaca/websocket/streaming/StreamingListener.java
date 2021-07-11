package net.jacobpeterson.alpaca.websocket.streaming;

import net.jacobpeterson.alpaca.model.endpoint.streaming.StreamingMessage;
import net.jacobpeterson.alpaca.model.endpoint.streaming.enums.StreamingMessageType;
import net.jacobpeterson.alpaca.websocket.AlpacaWebsocketMessageListener;

/**
 * {@link StreamingListener} defines a listener interface for {@link StreamingWebsocket} messages.
 */
public interface StreamingListener extends AlpacaWebsocketMessageListener<StreamingMessageType, StreamingMessage> {
}
