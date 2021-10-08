package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.*;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeZoneDefaultTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private static final LocalDate PREV_DATE = LocalDate.of(2021, 10, 1);
    private static final LocalDate DATE = LocalDate.of(2021, 10, 6);
    private static final YearWeek WEEK = YearWeek.of(2021, 40);

    @Test
    void previousTimeZoneInEmptyDayResponse() {
        dayCaptain.createDayTimeEvent("Event", time(PREV_DATE, 10, moscow), time(PREV_DATE, 13, berlin));
        assertThat(dayCaptain.getDay(DATE).prevZone).isEqualTo(berlin.getId());
        dayCaptain.createDayTimeEvent("Event", time(PREV_DATE, 18, berlin), time(PREV_DATE, 22, moscow));
        assertThat(dayCaptain.getDay(DATE).prevZone).isEqualTo(moscow.getId());
    }

    @Test
    void previousTimeZoneInEmptyWeekResponse() {
        dayCaptain.createDayTimeEvent("Event", time(PREV_DATE, 10, moscow), time(PREV_DATE, 13, berlin));
        assertThat(dayCaptain.getWeek(WEEK).prevZone).isEqualTo(berlin.getId());
        dayCaptain.createDayTimeEvent("Event", time(PREV_DATE, 18, berlin), time(PREV_DATE, 22, moscow));
        assertThat(dayCaptain.getWeek(WEEK).prevZone).isEqualTo(moscow.getId());
    }

    @BeforeEach
    @AfterEach
    void cleanUp() {
        dayCaptain.deleteDayTimeEvents(
                of(2021, 10, 1),
                of(2021, 10, 4),
                of(2021, 10, 5),
                of(2021, 10, 6),
                of(2021, 10, 7),
                of(2021, 10, 8),
                of(2021, 10, 9),
                of(2021, 10, 10)
        );
    }

}
