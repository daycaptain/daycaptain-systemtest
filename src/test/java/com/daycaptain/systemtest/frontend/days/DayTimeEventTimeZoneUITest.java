package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.daycaptain.systemtest.Times.*;

public class DayTimeEventTimeZoneUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);

    @Test
    void detect_last_time_zone_empty_day() {
        DayView day = dayCaptain.day(date);

        create(day, "New event", "10:00", "12:00", berlin, moscow);
        day.nextDay();

        CreateDayTimeEventAction action = day.timeEvents().create();
        action.assertTimeZone(moscow);
        action.close();
    }

    @Test
    void detect_previous_time_zone_switch_within_day() {
        DayView day = dayCaptain.day(date);

        create(day, "To Moscow", "02:00", "04:00", berlin, moscow);
        create(day, "To Lisbon", "06:00", "10:00", moscow, lisbon);
        create(day, "To Berlin", "14:00", "16:00", lisbon, berlin);

        CreateDayTimeEventAction action = day.timeEvents().create();
        action.setStartTime("01:00");
        action.assertTimeZone(berlin);
        action.setStartTime("04:30");
        action.assertTimeZone(moscow);
        action.setStartTime("11:30");
        action.assertTimeZone(lisbon);
        action.setStartTime("12:30");
        action.assertTimeZone(lisbon);
        action.setStartTime("18:00");
        action.assertTimeZone(berlin);
        action.setStartTime("22:00");
        action.assertTimeZone(berlin);
        action.close();
    }

    private void create(DayView day, String name, String startTime, String endTime, ZoneId startTimeZone, ZoneId endTimeZone) {
        CreateDayTimeEventAction action = day.timeEvents().create();
        action.setName(name);
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.save();
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
        system.deleteDayTimeEvents(date, date.plusDays(1));
    }

    @AfterAll
    static void afterAll() {
        system.deleteDayTimeEvents(date, date.plusDays(1));
        dayCaptain.close();
    }

}
