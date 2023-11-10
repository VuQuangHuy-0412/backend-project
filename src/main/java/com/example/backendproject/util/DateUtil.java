package com.example.backendproject.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static SimpleDateFormat yyyyMMddHHmmss_SSS = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
    public static SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateTimeFormatter dd_MM_yyyy_HH_mm_ss = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static LocalDate toLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date toDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static String formatddMMyyyy(Date date){
        return dd_MM_yyyy.format(date);
    }

    public static int generateDateKey(Date date) {
        LocalDate localDate = toLocalDate(date);
        return generateDateKey(localDate);
    }

    public static int generateDateKey(LocalDate date) {
        String month = date.getMonthValue() < 10 ? "0" + date.getMonthValue() : String.valueOf(date.getMonthValue());
        String day = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : String.valueOf(date.getDayOfMonth());

        return Integer.parseInt(date.getYear() + month + day);
    }

    public static int generateMonthKey(Date date) {
        LocalDate localDate = toLocalDate(date);
        String month = localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : String.valueOf(localDate.getMonthValue());

        return Integer.parseInt(localDate.getYear() + month);
    }

    public static String toString(LocalDate localDate) {
        return dd_MM_yyyy_HH_mm_ss.format(localDate.atStartOfDay());
    }

    public static String toString(LocalDateTime localDateTime) {
        return dd_MM_yyyy_HH_mm_ss.format(localDateTime);
    }

    public static String toString(Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toString(localDateTime);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return toDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return toDate(endOfDay);
    }
}
