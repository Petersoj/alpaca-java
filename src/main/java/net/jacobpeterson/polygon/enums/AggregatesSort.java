package net.jacobpeterson.polygon.enums;

import net.jacobpeterson.abstracts.enums.APIName;

public enum AggregatesSort implements APIName {
    ASC("asc"),
    DESC("desc");

    String apiName;

    AggregatesSort(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
