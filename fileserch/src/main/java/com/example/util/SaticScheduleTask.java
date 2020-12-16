package com.example.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@EnableScheduling
public class SaticScheduleTask {

    @Value("${file.temp}")
    private String fileTemp;

    @Value("${file.index}")
    private String INDEX_DIR;

    /**
     *
     * 晚上12点定时删除索引文件和生成临时html文件
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void statusScheduled() {
        if (clearFiles(INDEX_DIR)) {
            System.out.println("=====>>>>>删除索引在" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "成功");
        }
        if (clearFiles(fileTemp)) {
            System.out.println("=====>>>>>删除html临时文件在" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "成功");
        }
    }

    private static boolean clearFiles(String workspaceRootPath) {
        File file = new File(workspaceRootPath);
        if (file.exists()) {
            deleteFile(file);
        }
        return true;
    }

    /**
     * 递归删除文件
     * @param file
     */
    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

}