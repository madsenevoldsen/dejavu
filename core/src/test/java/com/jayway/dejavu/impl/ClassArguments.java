package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

public class ClassArguments {


    @Traced
    public void withClass( Class<ClassArguments> clazz ) {
        if ( ClassArguments.class.isAssignableFrom( clazz ) ) {
            System.out.println("itself!");
        }
    }
}
