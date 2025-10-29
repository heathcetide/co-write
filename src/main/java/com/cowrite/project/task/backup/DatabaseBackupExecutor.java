package com.cowrite.project.task.backup;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;

import javax.mail.internet.MimeMessage;
import java.io.File;

//@Component
public class DatabaseBackupExecutor {

    private final JavaMailSender mailSender;

    public DatabaseBackupExecutor(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    //    @Scheduled(cron = "0 0 0 * * ?")
    // 每分钟执行一次（可以更改为每天午夜等其他时间）
    @Scheduled(cron = "0 0/1 * * * ?")
    public void backupDatabaseAndSendEmail() throws Exception {
        System.out.println("开启备份.................................");
        // 获取当前工作目录
        String backupDir = System.getProperty("user.dir") + "/backups/";

        // 确保目录存在
        File backupDirectory = new File(backupDir);
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();  // 创建目录
        }

        // 备份数据库的命令
        String backupCommand = "mysqldump -h cd-cynosdbmysql-grp-lfa6zfg0.sql.tencentcdb.com -P 23771 -u root -p'CTct288513832##' cetide_blog > " + backupDir + "backup.sql";

        // 执行备份命令
        Process process = Runtime.getRuntime().exec(backupCommand);
        process.waitFor();

        // 将备份文件发送到指定邮箱
        sendEmailWithAttachment("2148582258@qq.com", "数据库备份文件", "请查看附件中的数据库备份文件", backupDir + "backup.sql");

        System.out.println("同步完成...........");
        // 删除本地备份文件（如果不需要保留本地副本）
        String deleteCommand = "rm -f " + backupDir + "backup.sql";
        Runtime.getRuntime().exec(deleteCommand);
    }

    private void sendEmailWithAttachment(String to, String subject, String text, String filePath) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // 添加附件
        File file = new File(filePath);
        helper.addAttachment("backup.sql", file);

        mailSender.send(message);
        System.out.println("数据库备份邮件发送成功！");
    }
}

