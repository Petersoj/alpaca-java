package net.jacobpeterson.alpaca.enums.streaming;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.alpaca.abstracts.enums.APIName;
import net.jacobpeterson.alpaca.util.gson.GsonUtil;

/**
 * {@link TradeUpdateEvent} defines enums for various trade update events.
 */
public enum TradeUpdateEvent implements APIName {

    /**
     * Sent when an order has been routed to exchanges for execution.
     */
    @SerializedName("new")
    NEW,

    /**
     * Sent when your order has been completely filled.
     */
    @SerializedName("fill")
    FILL,

    /**
     * Sent when a number of shares less than the total remaining quantity on your order has been filled.
     */
    @SerializedName("partial_fill")
    PARTIAL_FILL,

    /**
     * Sent when your requested cancellation of an order is processed.
     */
    @SerializedName("canceled")
    CANCELED,

    /**
     * Sent when an order has reached the end of its lifespan, as determined by the order’s time in force value.
     **/
    @SerializedName("expired")
    EXPIRED,

    /**
     * Sent when the order is done executing for the day, and will not receive further updates until the next trading
     * day.
     */
    @SerializedName("done_for_day")
    DONE_FOR_DAY,

    /**
     * Sent when your requested replacement of an order is processed.
     */
    @SerializedName("replaced")
    REPLACED,

    /**
     * Sent when your order has been rejected.
     */
    @SerializedName("rejected")
    REJECTED,

    /**
     * Sent when the order has been received by Alpaca and routed to the exchanges, but has not yet been accepted for
     * execution.
     */
    @SerializedName("pending_new")
    PENDING_NEW,

    /**
     * Sent when your order has been stopped, and a trade is guaranteed for the order, usually at a stated price or
     * better, but has not yet occurred.
     */
    @SerializedName("stopped")
    STOPPED,

    /**
     * Sent when the order is awaiting cancellation. Most cancellations will occur without the order entering this
     * state.
     */
    @SerializedName("pending_cancel")
    PENDING_CANCEL,

    /**
     * Sent when the order is awaiting replacement.
     */
    @SerializedName("pending_replace")
    PENDING_REPLACE,

    /**
     * Sent when the order has been completed for the day - it is either “filled” or “done_for_day” - but remaining
     * settlement calculations are still pending.
     */
    @SerializedName("calculated")
    CALCULATED,

    /**
     * Sent when the order has been suspended and is not eligible for trading.
     */
    @SerializedName("suspended")
    SUSPENDED,

    /**
     * Sent when the order replace has been rejected.
     */
    @SerializedName("order_replace_rejected")
    ORDER_REPLACE_REJECTED,

    /**
     * Sent when the order cancel has been rejected.
     */
    @SerializedName("order_cancel_rejected")
    ORDER_CANCEL_REJECTED;

    @Override
    public String getAPIName() {
        return GsonUtil.GSON.toJsonTree(this).getAsString();
    }
}
