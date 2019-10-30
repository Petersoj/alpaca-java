package io.github.mainstringargs.alpaca.enums;

/**
 * The enum Activity types.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account-activities/">https://docs.alpaca.markets/api-documentation/api-v2/account-activities/</a>
 */
public enum ActivityTypes {

    /*
     Activity Types:
     FILL: Order fills (both partial and full fills)
     TRANS: Cash transactions (both CSD and CSR)
     MISC: Miscellaneous or rarely used activity types (All types except those in TRANS, DIV, or FILL)
     ACATC: ACATS IN/OUT (Cash)
     ACATS: ACATS IN/OUT (Securities)
     CSD: Cash disbursement(+)
     CSR: Cash receipt(-)
     DIV: Dividends
     DIVCGL: Dividend (capital gain long term)
     DIVCGS: Dividend (capital gain short term)
     DIVFEE: Dividend fee
     DIVFT: Dividend adjusted (Foreign Tax Withheld)
     DIVNRA: Dividend adjusted (NRA Withheld)
     DIVROC: Dividend return of capital
     DIVTW: Dividend adjusted (Tefra Withheld)
     DIVTXEX: Dividend (tax exempt)
     INT: Interest (credit/margin)
     INTNRA Interest adjusted (NRA Withheld)
     INTTW: Interest adjusted (Tefra Withheld)
     JNL: Journal entry
     JNLC: Journal entry (cash)
     JNLS: Journal entry (stock)
     MA: Merger/Acquisition
     NC: Name change
     OPASN: Option assignment
     OPEXP: Option expiration
     OPXRC: Option exercise
     PTC: Pass Thru Charge
     PTR: Pass Thru Rebate
     REORG: Reorg CA
     SC: Symbol change
     SSO: Stock spinoff
     SSP: Stock split
     */

    // The names of these enums must be the API name exactly
    // @see ActivityTypes#getAPIName()

    /** Order fills (both partial and full fills) */
    FILL,

    /** Cash transactions (both CSD and CSR) */
    TRANS,
    /**
     * Miscellaneous or rarely used activity types
     * (All types except those in TRANS, DIV, or FILL)
     */
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

    /**
     * Gets the API name.
     *
     * @return the API name
     */
    public String getAPIName() {
        return this.name();
    }
}
