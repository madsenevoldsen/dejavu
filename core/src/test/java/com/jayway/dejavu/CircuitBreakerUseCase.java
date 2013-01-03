package com.jayway.dejavu;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.annotation.CircuitBreaker;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.value.LongValue;
import com.jayway.dejavu.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerUseCase extends UseCase<StringValue, Long>{

    @CircuitBreaker("Sick")
    @Autowire("SickProvider") Provider<Void, StringValue> sick;

    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public Long run(StringValue stringValue) {
        StringValue value = sick.request(null);
        Long time = timeStamp.request(null).getValue();
        throw new NullPointerException();
    }
}
