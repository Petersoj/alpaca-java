package net.jacobpeterson.alpaca.enums.activity;

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
    FILL,

    /** Cash transactions (both CSD and CSW) */
    TRANS,

    /** Miscellaneous or rarely used activity types (All types except those in TRANS, DIV, or FILL) */
    MISC,

    /** ACATS IN/OUT (Cash) */
    ACATC,

    /** ACATS IN/OUT (Securities) */
    ACATS,

    /** Cash deposit(+) */
    CSD,

    /** Cash receipt(-) */
    CSR,

    /** Cash withdrawal(-) */
    CSW,

    /** Dividends */
    DIV,

    /** Dividend (capital gain long term) */
    DIVCGL,

    /** Dividend (capital gain short term) */
    DIVCGS,

    /** Dividend fee */
    DIVFEE,

    /** Dividend adjusted (Foreign Tax Withheld) */
    DIVFT,

    /** Dividend adjusted (NRA Withheld) */
    DIVNRA,

    /** Dividend return of capital */
    DIVROC,

    /** Dividend adjusted (Tefra Withheld) */
    DIVTW,

    /** Dividend (tax exempt) */
    DIVTXEX,

    /** Interest (credit/margin) */
    INT,

    /** Interest adjusted (NRA Withheld) */
    INTNRA,

    /** Interest adjusted (Tefra Withheld) */
    INTTW,

    /** Journal entry */
    JNL,

    /** Journal entry (cash) */
    JNLC,

    /** Journal entry (stock) */
    JNLS,

    /** Merger/Acquisition */
    MA,

    /** Name change */
    NC,

    /** Option assignment */
    OPASN,

    /** Option expiration */
    OPEXP,

    /** Option exercise */
    OPXRC,

    /** Pass Thru Charge */
    PTC,

    /** Pass Thru Rebate */
    PTR,

    /** REG/TAF fees */
    FEE,

    /** Reorg CA */
    REORG,

    /** Symbol change */
    SC,

    /** Stock spinoff */
    SSO,

    /** Stock split */
    SSP;

    @Override
    public String getAPIName() {
        return this.name();
    }
}
