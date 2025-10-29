package com.cowrite.project.task.backup;

import com.cowrite.project.config.ProgressTracker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@Service
public class RestoreService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // 并行线程池

    public void restoreData() {
        // 恢复全量备份
        executorService.submit(() -> {
            try {
                MySQLRestoreUtil.restoreFullBackup("localhost", "3306", "root", "password", "your_database", "/data/backups/full_backup.sql");
                System.out.println("Full backup restoration completed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 应用增量备份
        executorService.submit(() -> {
            try {
                MySQLRestoreUtil.restoreIncrementalBackup("/data/backups/binlogs", "localhost", "3306", "root", "password");
                System.out.println("Incremental backup restoration completed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Resource
    private ProgressTracker progressTracker;

    public void restoreData(String taskId) {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(1000); // 模拟每步耗时
                    progressTracker.updateProgress(taskId, i); // 更新进度
                }
                progressTracker.removeProgress(taskId); // 任务完成后移除进度
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
