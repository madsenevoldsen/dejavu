package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.core.value.LongValue;
import com.jayway.dejavu.core.value.StringValue;
import com.jayway.dejavu.core.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkingUseCase extends UseCase<VoidValue, StringValue>{

    private Logger log = LoggerFactory.getLogger(WorkingUseCase.class);
    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public StringValue run(VoidValue input) {
        Long first = timeStamp.request(null).getValue();
        Long second = timeStamp.request(null).getValue();

        long span = second - first;
        return new StringValue( "My computer is so fast: " + span );
    }
}
