package com.benjaminsommer.dailygoals.util;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;

public class TimeHelperClass {

    public static int getIsoWeek(LocalDate date) {
        return date.getWeekOfWeekyear();
    }

    public static int getIsoWeekYear(LocalDate date) {
        return date.getWeekyear();
    }

    public static String getIsoWeekString(LocalDate date) {
        return String.valueOf(getIsoWeek(date)) + "/" + String.valueOf(getIsoWeekYear(date));
    }

    public static boolean compareCalWeeks(LocalDate localDate1, LocalDate localDate2) {
        return localDate1.getWeekOfWeekyear() == localDate2.getWeekOfWeekyear() && localDate1.getWeekyear() == localDate2.getWeekyear();
    }

    public static Calendar convertStringToCalendar(String strDate) {
        // date has to be in format yyyy-MM-dd
        String[] dateSplit = strDate.split("[./-]");
        int intYear = Integer.parseInt(dateSplit[0]);
        int intMonth = Integer.parseInt(dateSplit[1]);
        int intDate = Integer.parseInt(dateSplit[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(intYear, intMonth - 1, intDate);
        return calendar;
    }

    public static String convertCalendarToString(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        return sdf.format(date);
    }

}
