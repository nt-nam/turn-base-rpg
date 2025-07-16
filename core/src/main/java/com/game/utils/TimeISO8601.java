package com.game.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class TimeISO8601 {
    public static long get(){
        return System.currentTimeMillis();
    }
    public static String getISO8601String() {
        long currentTimeMillis = TimeUtils.millis();

        long currentTimeSeconds = currentTimeMillis / 1000;

        int days = (int)(currentTimeSeconds / (60 * 60 * 24));
        int years = days / 365;
        int months = (days % 365) / 30;
        int dayOfMonth = (days % 365) % 30;

        int hours = (int)((currentTimeSeconds % (60 * 60 * 24)) / 3600);
        int minutes = (int)((currentTimeSeconds % 3600) / 60);
        int seconds = (int)(currentTimeSeconds % 60);

        String isoDate = (1970 + years) + "-" + (months + 1) + "-" + dayOfMonth + "T" +
            hours + ":" + minutes + ":" + seconds + "Z";

        System.out.println(isoDate); // Ví dụ: 2025-07-04T12:45:30Z
        return isoDate;
    }
    public static double  parseDay(long currentTimeMillis){
        long currentTimeSeconds = currentTimeMillis / 1000;
        return (double) (currentTimeSeconds / (60 * 60 * 24));
    }

    // Chuyển đổi chuỗi ISO8601 thành timestamp (mili giây)
    private static long isoDateToTimestamp(String isoDate) {
        String[] dateTime = isoDate.replace("Z", "").split("T");
        String[] date = dateTime[0].split("-");
        String[] time = dateTime[1].split(":");

        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        int second = Integer.parseInt(time[2]);

        // Chuyển ngày thành mili giây
        long timestamp = 0;
        timestamp += (year - 1970) * 365L * 24 * 60 * 60 * 1000; // Số ngày từ 1970 đến năm hiện tại
        timestamp += (month - 1) * 30L * 24 * 60 * 60 * 1000; // Số tháng (30 ngày mỗi tháng)
        timestamp += (day - 1) * 24L * 60 * 60 * 1000; // Số ngày trong tháng
        timestamp += hour * 60L * 60 * 1000; // Giờ
        timestamp += minute * 60L * 1000; // Phút
        timestamp += second * 1000L; // Giây

        return timestamp;
    }
}
