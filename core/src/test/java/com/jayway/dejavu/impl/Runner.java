package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;

import java.util.Random;
import java.util.UUID;

public class Runner implements Runnable {

    public void run() {
        uuid();

        try {
            Thread.sleep(randomLong());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        uuid();
    }

    @Impure
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    @Impure
    public Long randomLong() {
        return Long.valueOf(new Random().nextInt(1000));
    }
}
