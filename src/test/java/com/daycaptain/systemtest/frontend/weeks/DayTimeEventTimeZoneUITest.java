package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.daycaptain.systemtest.Times.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DayTimeEventTimeZoneUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final YearWeek week = YearWeek.from(date);

    @Test
    void detect_last_time_zone_empty_week() {
        WeekView weekView = dayCaptain.week(week);

        weekView.dayTimeEvents(DayOfWeek.TUESDAY).createSave("New event", berlin, moscow, "10:00", "12:00");
        weekView.nextWeek();

        CreateDayTimeEventAction action = weekView.dayTimeEvents(DayOfWeek.MONDAY).create();
        action.assertTimeZone(moscow);
        action.close();
    }

    @Test
    void detect_previous_time_zone_switch_within_day() {
        WeekView weekView = dayCaptain.week(week);
        weekView.dayTimeEvents(DayOfWeek.TUESDAY);

        weekView.dayTimeEventsOffset(0).createSave("To Moscow", berlin, moscow, "01:30", "04:00");
        weekView.dayTimeEventsOffset(0).createSave("To Lisbon", moscow, lisbon, "06:00", "10:00");
        weekView.dayTimeEventsOffset(0).createSave("To Berlin", lisbon, berlin, "14:00", "16:00");

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

    @Test
    void edit_time_zones() {
        WeekView weekView = dayCaptain.week(week);
        weekView.dayTimeEvents(DayOfWeek.TUESDAY);

        weekView.dayTimeEventsOffset(0).createSave("New event", berlin, berlin, "09:00", "10:00");
        assertEdit(weekView, "09:00", "10:00", berlin);

        weekView.dayTimeEventsOffset(0).editSave(berlin, moscow, "01:30", "04:00");
        assertEdit(weekView, "01:30", "04:00", berlin, moscow);

        weekView.dayTimeEventsOffset(0).editSave(moscow, lisbon, "06:00", "10:00");
        assertEdit(weekView, "06:00", "10:00", moscow, lisbon);

        weekView.dayTimeEventsOffset(0).editSave(lisbon, berlin, "14:00", "16:00");
        assertEdit(weekView, "14:00", "16:00", lisbon, berlin);

        weekView.dayTimeEventsOffset(0).editSave(lisbon, moscow, "10:00", "11:00");
        assertEdit(weekView, "07:00", "11:00", lisbon, moscow);

        weekView.dayTimeEventsOffset(0).editSave("10:00", "14:00", lisbon, moscow);
        assertEdit(weekView, "10:00", "14:00", lisbon, moscow);
    }

    private void assertEdit(WeekView week, String startTime, String endTime, ZoneId zones) {
        EditTimeEventAction action = week.dayTimeEventsOffset(0).edit();
        assertThat(action.getStartTime()).isEqualTo(startTime);
        assertThat(action.getEndTime()).isEqualTo(endTime);
        action.assertTimeZone(zones);
        action.close();
    }

    private void assertEdit(WeekView week, String startTime, String endTime, ZoneId startZone, ZoneId endZone) {
        EditTimeEventAction action = week.dayTimeEventsOffset(0).edit();
        assertThat(action.getStartTime()).isEqualTo(startTime);
        assertThat(action.getEndTime()).isEqualTo(endTime);
        action.assertStartTimeZone(startZone);
        action.assertEndTimeZone(endZone);
        action.close();
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
