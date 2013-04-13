package com.jayway.dejavu.core.impl;

import com.jayway.dejavu.core.annotation.Impure;

import java.util.Enumeration;

public class ZipFileEnumeration implements Enumeration {

    private Enumeration enumeration;

    public ZipFileEnumeration( Enumeration enumeration ) {
        this.enumeration = enumeration;
    }

    @Override
    @Impure
    public boolean hasMoreElements() {
        return enumeration.hasMoreElements();
    }

    @Override
    @Impure
    public Object nextElement() {
        return enumeration.nextElement();
    }
}
