package com.daycaptain.systemtest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class Times {

    public static LocalDateTime time(LocalDate date, int hour) {
        return time(date, hour, 0);
    }

    public static LocalDateTime time(LocalDate date, int hour, int minute) {
        return LocalDateTime.of(date, LocalTime.of(hour, minute));
    }

}
