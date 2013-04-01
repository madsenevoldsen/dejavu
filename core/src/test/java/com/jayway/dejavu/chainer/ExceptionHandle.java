package com.jayway.dejavu.chainer;

public class ExceptionHandle implements Handle {

    @Override
    public String name(Class clazz) {
        if ( Exception.class.isAssignableFrom(clazz)) {
            return "Exception";
        }
        return null;
    }
}
