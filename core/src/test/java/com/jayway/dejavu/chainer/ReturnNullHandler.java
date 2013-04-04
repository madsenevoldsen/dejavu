package com.jayway.dejavu.chainer;

import com.jayway.dejavu.core.chainer.ChainBuilder;

public class ReturnNullHandler implements Handle {

    @Override
    public String name(Class clazz) {
        if ( Integer.class.isAssignableFrom( clazz ) ) {
            return "integer type";
        } else if ( String.class.isAssignableFrom( clazz )) {
            ChainBuilder.finished();
            return null;
        }
        return null;
    }
}
