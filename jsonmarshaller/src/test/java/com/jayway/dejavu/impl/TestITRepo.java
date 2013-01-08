package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.repository.UseCaseTestRepository;

public class TestITRepo implements UseCaseTestRepository {

    private Trace trace;

    @Override
    public void setEnableTraceForUseCase(Class<? extends UseCase<?, ?>> useCaseClass, Boolean enable) {
    }

    @Override
    public void addTrace(Trace trace) {
        this.trace = trace;
    }

    @Override
    public Boolean isTraceEnabled(Class<? extends UseCase<?, ?>> useCaseClass) {
        return true;
    }

    public Trace getTrace() {
        return trace;
    }
}
