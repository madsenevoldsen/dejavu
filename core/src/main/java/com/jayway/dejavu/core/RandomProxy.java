package com.jayway.dejavu.core;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RandomProxy implements MethodInterceptor {

    @Override
    public Object intercept(Object object, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
        // act as if all methods was annotated @Impure
        if ( DejaVuAspect.fallThrough() ) {
            return methodProxy.invokeSuper(object, arguments);
        }
        return DejaVuAspect.handle(null, new ProxyMethod(object, arguments, methodProxy), "");
    }
}
