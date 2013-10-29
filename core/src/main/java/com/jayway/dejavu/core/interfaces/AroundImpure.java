package com.jayway.dejavu.core.interfaces;

public interface AroundImpure {

    Object proceed( Interception interception ) throws Throwable;
}
