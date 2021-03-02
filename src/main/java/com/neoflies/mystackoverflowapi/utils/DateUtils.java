package com.neoflies.mystackoverflowapi.utils;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateUtils {
  public Date getStartOfDay() {
    return Date.from(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
  }

  public Date getEndOfDay() {
    return Date.from(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusDays(1).atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
  }

  public Date getStartOfWeek() {
    ZonedDateTime startOfWeek = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"));
    while (startOfWeek.getDayOfWeek() != DayOfWeek.MONDAY) {
      startOfWeek = startOfWeek.minusDays(1);
    }
    return Date.from(startOfWeek.toInstant());
  }

  public Date getEndOfWeek() {
    ZonedDateTime endOfWeek = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"));
    while (endOfWeek.getDayOfWeek() != DayOfWeek.SUNDAY) {
      endOfWeek = endOfWeek.plusDays(1);
    }
    endOfWeek.plusDays(1);
    return Date.from(endOfWeek.toInstant());
  }
}
