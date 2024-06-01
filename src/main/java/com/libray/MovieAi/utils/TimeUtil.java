package com.libray.MovieAi.utils;

public class TimeUtil {
    public static String formatRuntime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return hours + " giờ " + minutes + " phút";
    }
}