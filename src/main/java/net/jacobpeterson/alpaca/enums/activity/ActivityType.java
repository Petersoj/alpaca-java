package net.jacobpeterson.alpaca.enums.activity;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.abstracts.enums.APIName;

/**
 * {@link ActivityType} defines enums for various account activity types.
 *
 * @see
 * <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">https://docs.alpaca.markets/api-documentation/api-v2/account-activities/</a>
 */
public enum ActivityType implements APIName {

    // The names of these enums must be the API name exactly
    // @see ActivityType#getAPIName()

    /** Order fills (both partial and full fills) */
    @SerializedName("FILL")
    FILL,

    /** Cash transactions (both CSD and CSW) */
    @SerializedName("TRANS")
    TRANS,

    /** Miscellaneous or rarely used activity types (All types except those in TRANS, DIV, or FILL) */
    @SerializedName("MISC")
    MISC,

    /** ACATS IN/OUT (Cash) */
    @SerializedName("ACATC")
    ACATC,

    /** ACATS IN/OUT (Securities) */
    @SerializedName("ACATS")
    ACATS,

    /** Cash deposit(+) */
    @SerializedName("CSD")
    CSD,

    /** Cash receipt(-) */
    @SerializedName("CSR")
    CSR,

    /** Cash withdrawal(-) */
    @SerializedName("CSW")
    CSW,

    /** Dividends */
    @SerializedName("DIV")
    DIV,

    /** Dividend (capital gain long term) */
    @SerializedName("DIVCGL")
    DIVCGL,

    /** Dividend (capital gain short term) */
    @SerializedName("DIVCGS")
    DIVCGS,

    /** Dividend fee */
    @SerializedName("DIVFEE")
    DIVFEE,

    /** Dividend adjusted (Foreign Tax Withheld) */
    @SerializedName("DIVFT")
    DIVFT,

    /** Dividend adjusted (NRA Withheld) */
    @SerializedName("DIVNRA")
    DIVNRA,

    /** Dividend return of capital */
    @SerializedName("DIVROC")
    DIVROC,

    /** Dividend adjusted (Tefra Withheld) */
    @SerializedName("DIVTW")
    DIVTW,

    /** Dividend (tax exempt) */
    @SerializedName("DIVTXEX")
    DIVTXEX,

    /** Interest (credit/margin) */
    @SerializedName("INT")
    INT,

    /** Interest adjusted (NRA Withheld) */
    @SerializedName("INTNRA")
    INTNRA,

    /** Interest adjusted (Tefra Withheld) */
    @SerializedName("INTTW")
    INTTW,

    /** Journal entry */
    @SerializedName("JNL")
    JNL,

    /** Journal entry (cash) */
    @SerializedName("JNLC")
    JNLC,

    /** Journal entry (stock) */
    @SerializedName("JNLS")
    JNLS,

    /** Merger/Acquisition */
    @SerializedName("MA")
    MA,

    /** Name change */
    @SerializedName("NC")
    NC,

    /** Option assignment */
    @SerializedName("OPASN")
    OPASN,

    /** Option expiration */
    @SerializedName("OPEXP")
    OPEXP,

    /** Option exercise */
    @SerializedName("OPXRC")
    OPXRC,

    /** Pass Thru Charge */
    @SerializedName("PTC")
    PTC,

    /** Pass Thru Rebate */
    @SerializedName("PTR")
    PTR,

    /** Reorg CA */
    @SerializedName("REORG")
    REORG,

    /** Symbol change */
    @SerializedName("SC")
    SC,

    /** Stock spinoff */
    @SerializedName("SSO")
    SSO,

    /** Stock split */
    SSP;

    @Override
    public String getAPIName() {
        return this.name();
    }
}
