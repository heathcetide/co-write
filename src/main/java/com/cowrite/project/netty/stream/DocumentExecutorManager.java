package com.cowrite.project.netty.stream;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DocumentExecutorManager {

    private final Map<String, ExecutorService> docExecutors = new ConcurrentHashMap<>();

    public void execute(String docId, Runnable task) {
        docExecutors.computeIfAbsent(docId, id -> Executors.newSingleThreadExecutor())
                .submit(task);
    }
}
