package net.jacobpeterson.alpaca.enums;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;
import net.jacobpeterson.util.gson.GsonUtil;

/**
 * {@link PortfolioTimeFrame} defines enums for various
 * {@link net.jacobpeterson.domain.alpaca.portfoliohistory.PortfolioHistory}
 * time frames.
 */
public enum PortfolioTimeFrame implements APIName {

    /** 1 min {@link PortfolioTimeFrame}. */
    @SerializedName("1Min")
    ONE_MIN,

    /** 5 minute {@link PortfolioTimeFrame}. */
    @SerializedName("5Min")
    FIVE_MINUTE,

    /** 15 minute {@link PortfolioTimeFrame}. */
    @SerializedName("15Min")
    FIFTEEN_MINUTE,

    /** 1 hour {@link PortfolioTimeFrame}. */
    @SerializedName("1H")
    ONE_HOUR,

    /** 1 day {@link PortfolioTimeFrame}. */
    @SerializedName("1D")
    ONE_DAY;

    @Override
    public String getAPIName() {
        return GsonUtil.GSON.toJsonTree(this).getAsString();
    }
}
