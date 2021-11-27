package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.*;

public class DayTimeEventTimeZoneUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);

    @Test
    void detect_last_time_zone_empty_day() {
        DayView day = dayCaptain.day(date);

        day.timeEvents().createSave("New event", berlin, moscow, "10:00", "12:00");
        day.nextDay();

        CreateDayTimeEventAction action = day.timeEvents().create();
        action.assertTimeZone(moscow);
        action.close();
    }

    @Test
    void detect_previous_time_zone_switch_within_day() {
        DayView day = dayCaptain.day(date);

        day.timeEvents().createSave("To Moscow", berlin, moscow, "01:30", "04:00");
        day.timeEvents().createSave("To Lisbon", moscow, lisbon, "06:00", "10:00");
        day.timeEvents().createSave("To Berlin", lisbon, berlin, "14:00", "16:00");

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

    @Test
    void edit_time_zones() {
        DayView day = dayCaptain.day(date);

        day.timeEvents().createSave("New event", berlin, berlin, "09:00", "10:00");
        day.timeEvents().assertEdit("09:00", "10:00", berlin);

        day.timeEvents().editSave(berlin, moscow, "01:30", "04:00");
        day.timeEvents().assertEdit("01:30", "04:00", berlin, moscow);

        day.timeEvents().editSave(moscow, lisbon, "06:00", "10:00");
        day.timeEvents().assertEdit("06:00", "10:00", moscow, lisbon);

        day.timeEvents().editSave(lisbon, berlin, "14:00", "16:00");
        day.timeEvents().assertEdit("14:00", "16:00", lisbon, berlin);

        day.timeEvents().editSave(lisbon, moscow, "10:00", "11:00");
        day.timeEvents().assertEdit("07:00", "11:00", lisbon, moscow);

        day.timeEvents().editSave("10:00", "14:00", lisbon, moscow);
        day.timeEvents().assertEdit("10:00", "14:00", lisbon, moscow);
    }

    @Test
    void edit_only_time_zone() {
        DayView day = dayCaptain.day(date);

        day.timeEvents().createSave("New event", berlin, berlin, "09:00", "10:00");
        day.timeEvents().assertEdit("09:00", "10:00", berlin);

        EditTimeEventAction edit = day.timeEvents().edit();
        edit.setEndTimeZone(moscow);
        edit.save();

        day.timeEvents().assertEdit("09:00", "12:00", berlin, moscow);

        edit = day.timeEvents().edit();
        edit.setStartTimeZone(moscow);
        edit.save();
        day.timeEvents().assertEdit("09:00", "12:00", moscow, moscow);
    }

    @Test
    void switch_single_time_zone() {
        DayView day = dayCaptain.day(date);

        day.timeEvents().createSave("New event", berlin, "09:00", "10:00");
        day.timeEvents().assertEdit("09:00", "10:00", berlin);

        EditTimeEventAction edit = day.timeEvents().edit();
        edit.setZones(moscow);
        edit.save();

        day.timeEvents().assertEdit("11:00", "12:00", moscow);

        edit = day.timeEvents().edit();
        edit.setZones(berlin);
        edit.save();
        day.timeEvents().assertEdit("09:00", "10:00", berlin);
    }

    @BeforeEach
    @AfterEach
    void cleanUp() {
        system.deleteDayTimeEvents(date, date.plusDays(1));
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
