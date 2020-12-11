package net.jacobpeterson.alpaca.enums;

import net.jacobpeterson.abstracts.enums.APIName;

/**
 * The enum {@link AccountStatus}.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account#account-status">Account Status</a>
 */
public enum AccountStatus implements APIName {

    /**
     * The account is onboarding.
     */
    ONBOARDING("ONBOARDING"),

    /**
     * The account application submission failed for some reason.
     */
    SUBMISSION_FAILED(""),

    /**
     * The account application has been submitted for review.
     */
    SUBMITTED("SUBMITTED"),

    /**
     * The account information is being updated.
     */
    ACCOUNT_UPDATED("ACCOUNT_UPDATED"),

    /**
     * The final account approval is pending.
     */
    APPROVAL_PENDING("APPROVAL_PENDING"),

    /**
     * The account is active for trading.
     */
    ACTIVE("ACTIVE"),

    /**
     * The account application has been rejected.
     */
    REJECTED("REJECTED");

    /** The API name. */
    String apiName;

    /**
     * Instantiates a new {@link AccountStatus}.
     *
     * @param apiName the api name
     */
    AccountStatus(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}

