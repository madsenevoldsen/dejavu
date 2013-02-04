package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.AttachThread;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WithThreads {

    private static ExecutorService executorService;


    public static void initialize() {
        executorService = Executors.newCachedThreadPool();
    }

    @Traced
    public void begin( Integer threads) {
        initialize();
        for ( int i=0; i<threads; i++ ) {
            runInThreadPool(new Runner());
        }
    }
    
    @AttachThread
    private void runInThreadPool( Runnable runnable ) {
        executorService.submit(runnable);
    }
}
