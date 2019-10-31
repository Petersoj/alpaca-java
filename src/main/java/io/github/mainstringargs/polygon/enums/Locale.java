package io.github.mainstringargs.polygon.enums;

/**
 * The Enum SymbolsLocale.
 */
public enum Locale {

    /** The us. */
    US("us"),

    /** The global. */
    GLOBAL("g");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order type.
     *
     * @param apiName the api name
     */
    Locale(String apiName) {
        this.apiName = apiName;
    }

    /**
     * From API name.
     *
     * @param apiName the api name
     * @return the channel type
     */
    public static Locale fromAPIName(String apiName) {
        String apiNameString = apiName.trim();

        for (Locale cType : Locale.values()) {
            if (apiNameString.equals(cType.apiName)) {
                return cType;
            }
        }

        return null;
    }

    /**
     * Gets the API name.
     *
     * @return the API name
     */
    public String getAPIName() {
        return apiName;
    }
}
