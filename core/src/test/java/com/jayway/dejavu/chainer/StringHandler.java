package com.jayway.dejavu.chainer;

public class StringHandler implements Handle {

    @Override
    public String name(Class clazz) {
        if ( String.class.isAssignableFrom( clazz )) {
            return "String class";
        }
        return null;
    }
}
