package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The enum Account status.
 *
 * @see <a href="https://docs.alpaca.markets/api-documentation/api-v2/account#account-status">Account Status</a>
 */
public enum AccountStatus implements APIName {

    // The names of these enums must be the API name exactly
    // @see AccountStatus#getAPIName()

    /**
     * The account is onboarding.
     */
    ONBOARDING,

    /**
     * The account application submission failed for some reason.
     */
    SUBMISSION_FAILED,

    /**
     * The account application has been submitted for review.
     */
    SUBMITTED,

    /**
     * The account information is being updated.
     */
    ACCOUNT_UPDATED,

    /**
     * The final account approval is pending.
     */
    APPROVAL_PENDING,

    /**
     * The account is active for trading.
     */
    ACTIVE,

    /**
     * The account application has been rejected.
     */
    REJECTED;

    @Override
    public String getAPIName() {
        return this.name();
    }
}

