package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.Traced;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WithThreads {

    @Traced
    public void begin( Integer threads) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        for ( int i=0; i<threads; i++ ) {
            executorService.submit(new Runner());
        }
    }
}
