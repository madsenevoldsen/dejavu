package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleFailingIntegrationPoint {

    private Logger log = LoggerFactory.getLogger( ExampleFailingIntegrationPoint.class );

    @Traced
    public void run( String first, String second ) {
        log.info( "received: "+first+", and: "+second);
        impossible();
    }

    @Impure
    private Integer impossible() {
        return 4/0;
    }
}
