/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2020
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package net.romvoid.discord.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class Logger.
 */
public class Logger {
    
    /** The log file. */
    private static File logFile;
    
    /** The logger text. */
    private static String loggerText = "";
    
    /** The file logging. */
    private static boolean fileLogging = false;
    
    /** The log date formatter. */
    private static SimpleDateFormat logDateFormatter = new SimpleDateFormat("HH:mm:ss");

    /**
     * Log in file.
     *
     * @param appName the app name
     * @param appVersion the app version
     * @param logDirectory the log directory
     */
    public static void logInFile(String appName, String appVersion, String logDirectory) {
        String date = new SimpleDateFormat("dd_MM_yyyy-HH:mm:ss").format(new Date());
        String filename = new SimpleDateFormat("dd_MM_yyyy HH_mm").format(new Date());
        File newFile = new File(logDirectory + filename + ".log");
        try {
            if (!newFile.exists())
                newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logFile = newFile;
        if (!logFile.exists())
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        fileLogging = true;
        String logHeader = ("---- " + appName + " " + appVersion + " Log ----\n") +
                "\n" +
                "Date: " + date + "\n" +
                "\n" +
                "-- System Details --\n" +
                "Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version") + "\n" +
                "Java Version: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor") + "\n" +
                "Java VM Version: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor") + "\n" +
                "Memory: " + getMemoryText() + "\n" +
                "\n" +
                "-- Log --\n";
        addLogEntry(logHeader);
    }

    /**
     * Adds the log entry.
     *
     * @param text the text
     */
    private static void addLogEntry(String text) {
        if (!fileLogging)
            return;
        try {
            loggerText += text;
            FileWriter writer = new FileWriter(logFile);
            writer.write(loggerText);
            writer.close();
            //latest
            FileWriter writer2 = new FileWriter("latest.log");
            writer2.write(loggerText);
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Info.
     *
     * @param text the text
     */
    public static void info(String text) {
        log(text, LoggerLevel.INFO);
    }

    /**
     * Debug.
     *
     * @param text the text
     */
    public static void debug(String text) {
        log(text, LoggerLevel.DEBUG);
    }

    /**
     * Warning.
     *
     * @param text the text
     */
    public static void warning(String text) {
        log(text, LoggerLevel.WARNING);
    }

    /**
     * Error.
     *
     * @param text the text
     */
    public static void error(String text) {
        log(text, LoggerLevel.ERROR);
    }

    /**
     * Error.
     *
     * @param t the t
     */
    public static void error(Throwable t) {
        t.printStackTrace();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        t.printStackTrace(printWriter);
        log(stringWriter.getBuffer().toString(), LoggerLevel.THROWABLE);
    }

    /**
     * Log.
     *
     * @param text the text
     * @param loggerLevel the logger level
     */
    public static void log(String text, LoggerLevel loggerLevel) {
        switch (loggerLevel) {
            case INFO:
                String infoMessage = formatLogMessage("Info", text);
                System.out.println(infoMessage);
                addLogEntry(infoMessage + "\n");
                break;
            case DEBUG:
                String debugMessage = formatLogMessage("Debug", text);
                System.out.println(debugMessage);
                addLogEntry(debugMessage + "\n");
                break;
            case ERROR:
                String errorMessage = formatLogMessage("Error", text);
                System.err.println(errorMessage);
                addLogEntry(errorMessage + "\n");
                break;
            case THROWABLE:
                addLogEntry(formatLogMessage("Error", text) + "\n");
                break;
            case WARNING:
                String warningMessage = formatLogMessage("Warning", text);
                System.err.println(warningMessage);
                addLogEntry(warningMessage + "\n");
                break;
            default:
                break;
        }
    }

    /**
     * Format log message.
     *
     * @param logType the log type
     * @param text the text
     * @return the string
     */
    private static String formatLogMessage(String logType, String text) {
        return '[' + logType + "] (" + logDateFormatter.format(new Date()) + ") | " + text;
    }

    /**
     * Gets the memory text.
     *
     * @return the memory text
     */
    private static String getMemoryText() {
        Runtime var1 = Runtime.getRuntime();
        long maxMemory = var1.maxMemory();
        long totalMemory = var1.totalMemory();
        long freeMemory = var1.freeMemory();
        long maxMemoryInMB = maxMemory / 1024L / 1024L;
        long totalMemoryInMB = totalMemory / 1024L / 1024L;
        long freeMemoryInMB = freeMemory / 1024L / 1024L;

        return String.valueOf(freeMemory) + " bytes (" + freeMemoryInMB + " MB) / " +
                totalMemory + " bytes (" + totalMemoryInMB + " MB) up to " +
                maxMemory + " bytes (" + maxMemoryInMB + " MB)";
    }

    /**
     * Gets the log file.
     *
     * @return the log file
     */
    public static File getLogFile() {
        return logFile;
    }

    /**
     * The Enum LoggerLevel.
     */
    public enum LoggerLevel {
        
        /** The info. */
        INFO, 
 /** The debug. */
 DEBUG, 
 /** The error. */
 ERROR, 
 /** The throwable. */
 THROWABLE, 
 /** The warning. */
 WARNING
    }
}