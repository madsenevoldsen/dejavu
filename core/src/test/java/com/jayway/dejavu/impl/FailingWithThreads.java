package com.jayway.dejavu.impl;

import com.jayway.dejavu.core.annotation.AttachThread;
import com.jayway.dejavu.core.annotation.Traced;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FailingWithThreads {

    private static ExecutorService executorService;

    @Traced
    public void begin( Integer threads) {
        executorService = Executors.newCachedThreadPool();
        for ( int i=0; i<threads; i++ ) {
            runInThreadPool(new RunnerA());
        }
    }
    
    @AttachThread
    public static void runInThreadPool( Runnable runnable ) {
        executorService.submit(runnable);
    }
}
