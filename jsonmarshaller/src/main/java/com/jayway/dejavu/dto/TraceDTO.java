package com.jayway.dejavu.dto;

import java.util.List;

public class TraceDTO {
    private String useCaseClass;
    private List<TracedElementDTO> tracedElements;

    public String getUseCaseClass() {
        return useCaseClass;
    }

    public void setUseCaseClass(String useCaseClass) {
        this.useCaseClass = useCaseClass;
    }

    public List<TracedElementDTO> getTracedElements() {
        return tracedElements;
    }

    public void setTracedElements(List<TracedElementDTO> tracedElements) {
        this.tracedElements = tracedElements;
    }
}
