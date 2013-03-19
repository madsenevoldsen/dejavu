package com.jayway.dejavu.impl;

import java.util.Random;
import java.util.UUID;

public class Runner implements Runnable {

    public void run() {
        UUID.randomUUID().toString();
        try {
            Thread.sleep((long) new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UUID.randomUUID().toString();
    }
}
