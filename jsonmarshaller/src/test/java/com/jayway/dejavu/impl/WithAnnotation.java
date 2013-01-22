package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

public class WithAnnotation {

    public enum State {
        first, second, third
    }

    @Traced
    public void run( State state ) {

    }
}
