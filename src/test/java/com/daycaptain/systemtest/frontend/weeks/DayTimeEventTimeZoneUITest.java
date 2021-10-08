package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.daycaptain.systemtest.Times.*;

public class DayTimeEventTimeZoneUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final YearWeek week = YearWeek.from(date);

    @Test
    void detect_last_time_zone_empty_week() {
        WeekView weekView = dayCaptain.week(week);

        create(weekView, "New event", "10:00", "12:00", berlin, moscow);
        weekView.nextWeek();

        CreateDayTimeEventAction action = weekView.dayTimeEvents(DayOfWeek.MONDAY).create();
        action.assertTimeZone(moscow);
        action.close();
    }

    @Test
    void detect_previous_time_zone_switch_within_day() {
        WeekView weekView = dayCaptain.week(week);
        weekView.dayTimeEvents(DayOfWeek.TUESDAY);

        create(weekView, "To Moscow", "01:30", "04:00", berlin, moscow);
        create(weekView, "To Lisbon", "06:00", "10:00", moscow, lisbon);
        create(weekView, "To Berlin", "14:00", "16:00", lisbon, berlin);

        CreateDayTimeEventAction action = weekView.dayTimeEventsOffset(0).create();
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

    private void create(WeekView week, String name, String startTime, String endTime, ZoneId startTimeZone, ZoneId endTimeZone) {
        CreateDayTimeEventAction action = week.dayTimeEventsOffset(0).create();
        action.setName(name);
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.save();
    }

    @BeforeEach
    @AfterEach
    void cleanUp() {
        system.deleteDayTimeEvents(date.minusDays(1), date, date.plusDays(1));
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

}
