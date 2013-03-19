package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

import java.util.Random;

public class RandomProxyExample {

    @Traced
    public int invoke() {
        Random random = new Random();
        return random.nextInt();
    }
}
