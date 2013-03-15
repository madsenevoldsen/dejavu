package com.jayway.dejavu.core.factories;

import com.jayway.dejavu.core.DejaVuAspect;
import com.jayway.dejavu.core.RandomProxy;
import net.sf.cglib.proxy.Enhancer;

import java.util.Random;

public class RandomFactory {

    public static Random newRandom() {
        // Force ignore of anything during construction of the proxy,
        // otherwise the trace will contain a call to setSeed which is called during construction
        DejaVuAspect.setIgnore( true );
        Random random = (Random) Enhancer.create(Random.class, new RandomProxy());
        DejaVuAspect.setIgnore( false );
        return random;
    }
}
