package com.junjie.githelper.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 简单的日志工具类
 */
public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean debugEnabled = true;

    public static void info(String message) {
        log("INFO", message);
    }

    public static void debug(String message) {
        if (debugEnabled) {
            log("DEBUG", message);
        }
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Throwable throwable) {
        log("ERROR", message);
        throwable.printStackTrace();
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(String.format("[%s] [%s] %s", timestamp, level, message));
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
}

