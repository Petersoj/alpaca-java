package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum Activity type.
 *
 * @see
 * <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">https://docs.alpaca.markets/api-documentation/api-v2/account-activities/</a>
 */
public enum ActivityType implements APIName {

    /** Order fills (both partial and full fills) */
    FILL,

    /** Cash transactions (both CSD and CSR) */
    TRANS,

    /** Miscellaneous or rarely used activity types (All types except those in TRANS, DIV, or FILL) */
    MISC,

    /** ACATS IN/OUT (Cash) */
    ACATC,

    /** ACATS IN/OUT (Securities) */
    ACATS,

    /** Cash disbursement(+) */
    CSD,

    /** Cash receipt(-) */
    CSR,

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
