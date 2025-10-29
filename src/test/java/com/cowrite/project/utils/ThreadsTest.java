package com.cowrite.project.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadsTest {

    @Test
    public void testSleepDoesNotThrow() {
        assertDoesNotThrow(() -> Threads.sleep(100));
    }

    @Test
    public void testShutdownAndAwaitTerminationWithRunningPool() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> Threads.sleep(50));
        Threads.shutdownAndAwaitTermination(pool);
        assertTrue(pool.isShutdown());
    }

    @Test
    public void testShutdownAndAwaitTerminationWithNull() {
        assertDoesNotThrow(() -> Threads.shutdownAndAwaitTermination(null));
    }

    @Test
    public void testPrintExceptionWithExceptionRunnable() {
        Runnable errorTask = () -> {
            throw new RuntimeException("Expected Exception");
        };
        Throwable throwable = new RuntimeException("Expected Exception");
        Threads.printException(errorTask, throwable);
    }

    @Test
    public void testPrintExceptionWithFutureException() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            throw new IllegalStateException("Future Exception");
        });

        Threads.sleep(100); // 等待任务执行

        Threads.shutdownAndAwaitTermination(executor);
    }

    @Test
    public void testPrintExceptionWithCompletedFutureWithoutException() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            // 正常完成
        });

        Threads.sleep(50);

        Threads.shutdownAndAwaitTermination(executor);
    }
}
