package com.msrts.hostel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static String getPdfPathBasedOnOS() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            String documentsPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            Path directoryPath = Paths.get(documentsPath + "/" + formatter.format(new Date()));
            boolean exists = Files.exists(directoryPath);
            LOGGER.info("Windows OS PDF storage location: {}", directoryPath);
            if (exists) {
                return directoryPath + "/";
            } else {
                File file = new File(String.valueOf(directoryPath));
                boolean res = file.mkdir();
                String path = file.getAbsolutePath();
                if (res)
                    LOGGER.info("Windows directory created with the path of {}", path);
                return path;
            }
        } else {
            Path directoryPath = Paths.get(System.getProperty("user.home") + formatter.format(new Date()));
            boolean exists = Files.exists(directoryPath);
            LOGGER.info("PDF storage location: {}", directoryPath);
            if (exists) {
                return directoryPath + "/";
            } else {
                File file = new File(String.valueOf(directoryPath));
                boolean res = file.mkdir();
                String path = file.getAbsolutePath();
                if (res)
                    LOGGER.info("Directory created with the path of {}", path);
                return path;
            }
        }
    }
}
