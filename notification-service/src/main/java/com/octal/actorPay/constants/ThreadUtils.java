package com.octal.actorPay.constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    final static int MAX_T = 10;
    private final static ExecutorService threadPool = Executors.newFixedThreadPool(MAX_T);

    public static void executeThreadPool(final Runnable command) {
        threadPool.execute(command);
    }

    final static int MAX_PRIMARY_THREAD_POOL = 5;
    private final static ExecutorService primaryThreadPool = Executors.newFixedThreadPool(MAX_PRIMARY_THREAD_POOL);

    public static void executePrimaryThreadPool(final Runnable command) {
        primaryThreadPool.execute(command);
    }
}
