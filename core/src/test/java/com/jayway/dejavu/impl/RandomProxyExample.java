package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;
import com.jayway.dejavu.core.factories.RandomFactory;

import java.util.Random;

public class RandomProxyExample {

    @Traced
    public int invoke() {
        Random random = RandomFactory.newRandom();
        return random.nextInt();
    }
}
