package com.bot;

import java.text.SimpleDateFormat;

/**
 * Basic util function to print with timestamps.
 * TODO: Add these to an actual output log file one day.
 */
public class Logging {

    private static String getTimestamp() {
        return new SimpleDateFormat("HH:mm:ss MM/dd/yyyy").format(new java.util.Date());
    }

    public static void log(String str) {
        System.out.println("\t[" + getTimestamp() + "]: " + str);
    }

    public static void log(String title, String str) {
        System.out.println("\t[" + getTimestamp() + "] [" + title + "]: " + str);
    }
}
