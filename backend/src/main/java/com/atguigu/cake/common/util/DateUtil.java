package com.atguigu.cake.common.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public final class DateUtil {

    private DateUtil() {
    }

    /**
     * 日期加天数（按自然日）
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 两个日期相差的自然日天数（to - from），按日期部分计算，忽略时分秒
     */
    public static long daysBetween(Date from, Date to) {
        LocalDate fromDate = toLocalDate(from);
        LocalDate toDate = toLocalDate(to);
        return ChronoUnit.DAYS.between(fromDate, toDate);
    }

    private static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
