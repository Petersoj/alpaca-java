package io.github.mainstringargs.alpaca.websocket.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.mainstringargs.alpaca.enums.MessageType;
import io.github.mainstringargs.alpaca.enums.OrderEvent;
import io.github.mainstringargs.domain.alpaca.order.Order;
import io.github.mainstringargs.util.time.TimeUtil;

import java.time.LocalDateTime;

/**
 * The Class OrderUpdateMessage.
 */
public class OrderUpdateMessage implements UpdateMessage {

    private static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gson = gsonBuilder.create();
    }

    /** The event. */
    private OrderEvent event;

    /** The price. */
    private Double price;

    /** The timestamp. */
    private LocalDateTime timestamp;

    /** The order. */
    private Order order;

    /**
     * Instantiates a new order update message.
     *
     * @param data the data
     */
    public OrderUpdateMessage(JsonObject data) {
        event = OrderEvent.NEW;
        if (data.has("event")) {
            String jsonEvent = data.get("event").getAsString();
            event = OrderEvent.fromAPIName(jsonEvent);
        }

        price = null;
        if (data.has("price")) {
            String jsonPrice = data.get("price").getAsString();
            price = Double.parseDouble(jsonPrice);
        }

        timestamp = null;
        if (data.has("timestamp")) {
            String jsonTimeStamp = data.get("timestamp").getAsString();
            timestamp = TimeUtil.fromDateTimeString(jsonTimeStamp);
        }

        order = null;
        if (data.has("order")) {
            JsonObject jsonOrder = data.get("order").getAsJsonObject();

            order = gson.fromJson(jsonOrder, Order.class);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OrderUpdateMessage [event=" + event + ", price=" + price + ", timestamp=" + timestamp
                + ", order=" + order + "]";
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
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
        OrderUpdateMessage other = (OrderUpdateMessage) obj;
        if (event != other.event)
            return false;
        if (order == null) {
            if (other.order != null)
                return false;
        } else if (!order.equals(other.order))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    /**
     * Gets the event.
     *
     * @return the event
     */
    public OrderEvent getEvent() {
        return event;
    }

    /**
     * Gets the price.
     *
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public Order getOrder() {
        return order;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.github.mainstringargs.alpaca.websocket.message.UpdateMessage#getMessageType()
     */
    @Override
    public MessageType getMessageType() {
        return MessageType.ORDER_UPDATES;
    }
}
