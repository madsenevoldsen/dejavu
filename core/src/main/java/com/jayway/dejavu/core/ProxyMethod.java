package com.jayway.dejavu.core;

import net.sf.cglib.proxy.MethodProxy;

public class ProxyMethod {

    private Object object;
    private Object[] arguments;
    private MethodProxy methodProxy;

    public ProxyMethod(Object object, Object[] arguments, MethodProxy methodProxy) {
        this.object = object;
        this.arguments = arguments;
        this.methodProxy = methodProxy;
    }

    public Object invoke() throws Throwable {
        return methodProxy.invokeSuper( object, arguments );
    }
}
