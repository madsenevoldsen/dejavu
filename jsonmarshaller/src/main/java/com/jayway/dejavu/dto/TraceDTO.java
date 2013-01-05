package com.jayway.dejavu.dto;

import java.util.List;

public class TraceDTO {
    private String id;
    private Long time;
    private Boolean resolved;
    private String useCaseClass;
    private List<TracedElementDTO> tracedElements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

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
