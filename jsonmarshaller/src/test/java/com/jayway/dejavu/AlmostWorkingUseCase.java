package com.jayway.dejavu;

import com.jayway.dejavu.annotation.Autowire;
import com.jayway.dejavu.core.Provider;
import com.jayway.dejavu.core.UseCase;
import com.jayway.dejavu.value.LongValue;
import com.jayway.dejavu.value.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlmostWorkingUseCase extends UseCase<VoidValue, Void>{

    private Logger log = LoggerFactory.getLogger( AlmostWorkingUseCase.class);
    @Autowire("Timestamp") Provider<Void, LongValue> timeStamp;

    @Override
    public Void run(VoidValue input) {
        Long value = timeStamp.request(null).getValue();
        Long luckyNumber = 0l;
        if ( value % 1001 == 0 ) {
            luckyNumber = 2304432 / ( value % 1001 );
        } else {
            luckyNumber = 42L;
        }
        log.info( "My lucky number is: "+luckyNumber);
        return null;
    }
}
