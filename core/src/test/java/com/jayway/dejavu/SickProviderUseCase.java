package com.jayway.dejavu;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.value.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SickProviderUseCase extends UseCase<StringValue, String>{

    private Logger log = LoggerFactory.getLogger(SickProviderUseCase.class);

    @Autowire("SickProvider") Provider<Void, StringValue> sick;

    @Override
    public String run(StringValue stringValue) {
        sick.request(null);
        return null;
    }
}
