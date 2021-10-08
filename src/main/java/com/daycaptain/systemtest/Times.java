package com.daycaptain.systemtest;

import java.time.*;

public final class Times {

    public static final ZoneId berlin = ZoneId.of("Europe/Berlin");
    public static final ZoneId moscow = ZoneId.of("Europe/Moscow");
    public static final ZoneId lisbon = ZoneId.of("Europe/Lisbon");

    public static LocalTime time(int hour) {
        return LocalTime.of(hour, 0);
    }

    public static LocalTime time(int hour, int minute) {
        return LocalTime.of(hour, minute);
    }

    public static LocalDateTime time(LocalDate date, int hour) {
        return time(date, hour, 0);
    }

    public static LocalDateTime time(LocalDate date, int hour, int minute) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

    public static ZonedDateTime time(LocalDate date, int hour, ZoneId zoneId) {
        return ZonedDateTime.of(time(date, hour, 0), zoneId);
    }

    public static ZonedDateTime time(LocalDate date, int hour, int minute, ZoneId zoneId) {
        return ZonedDateTime.of(time(date, hour, minute), zoneId);
    }

}
