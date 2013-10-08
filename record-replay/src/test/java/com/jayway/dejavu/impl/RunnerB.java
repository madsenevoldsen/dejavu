package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;

import java.util.Random;

public class RunnerB implements Runnable {

    @Override
    public void run() {
        Integer integer = randomInt();

        throw new RuntimeException();
    }


    @Impure
    private Integer randomInt() {
        return new Random().nextInt(10);
    }
}
