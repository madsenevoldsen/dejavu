package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;
import com.jayway.dejavu.core.annotation.Traced;

public class RecurseAndExcept {

    @Traced
    public void recurse( int i ) {
        check(i);
        recurse( i+1 );
    }

    @Impure
    private void check( int i ) {
        if ( i > 10 ) {
            throw new RuntimeException("I is too big");
        }
    }
}
