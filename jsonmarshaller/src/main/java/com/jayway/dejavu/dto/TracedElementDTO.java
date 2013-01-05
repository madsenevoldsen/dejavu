package com.jayway.dejavu.dto;

public class TracedElementDTO {
    private String jsonValue;
    private String valueClass;

    public TracedElementDTO() {}
    public TracedElementDTO( String jsonValue, String valueClass) {
        this.jsonValue = jsonValue;
        this.valueClass = valueClass;
    }
    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }
}
