package com.jayway.dejavu.dto;

import java.util.List;

public class TraceDTO {
    private String className;
    private String methodName;
    private String[] argumentClasses;
    private String[] argumentJsonValues;
    private List<TracedElementDTO> values;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getArgumentClasses() {
        return argumentClasses;
    }

    public void setArgumentClasses(String[] argumentClasses) {
        this.argumentClasses = argumentClasses;
    }

    public String[] getArgumentJsonValues() {
        return argumentJsonValues;
    }

    public void setArgumentJsonValues(String[] argumentJsonValues) {
        this.argumentJsonValues = argumentJsonValues;
    }

    public List<TracedElementDTO> getValues() {
        return values;
    }

    public void setValues(List<TracedElementDTO> values) {
        this.values = values;
    }
}
