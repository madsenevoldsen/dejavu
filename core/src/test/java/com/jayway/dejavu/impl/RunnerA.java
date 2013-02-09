package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Impure;

import java.util.Random;
import java.util.UUID;

public class RunnerA implements Runnable {

    @Override
    public void run() {
        Long aLong = randomLong();
        try {
            Thread.sleep(aLong);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        uuid();
        if ( aLong % 2 == 0 ) {
            // launch runner B
            FailingWithThreads.runInThreadPool( new RunnerB());
        }
    }

    @Impure
    private String uuid() {
        return UUID.randomUUID().toString();
    }

    @Impure
    public Long randomLong() {
        return Long.valueOf(new Random().nextInt(1000));
    }

}
