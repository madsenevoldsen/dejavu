package com.jayway.dejavu.core.repository;

import com.jayway.dejavu.core.Trace;
import com.jayway.dejavu.core.UseCase;

public interface UseCaseTestRepository {
    void setEnableTraceForUseCase( Class<? extends UseCase<?,?>> useCaseClass, Boolean enable );
    void addTrace( Trace trace );
    Boolean isTraceEnabled( Class<? extends UseCase<?,?>> useCaseClass );
}
