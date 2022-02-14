package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;

class DayFilterUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final LocalDate date = LocalDate.of(2022, 2, 13);

    @Test
    void filter_tasks_time_events_header_shown() {
        DayView day = dayCaptain.day(date);

        day.assertNoFilter();
        assertThat(day.tasks().getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(day.timeEvents().getNames()).containsExactly("Event", "Event, project", "Event, other project", "Event, area", "Event, other area");
        assertThat(day.dayEvents().getNames()).containsExactly("Day event", "Second day event");

        day.filter().area("Business");

        day.assertAreaFilter("Business");
        assertThat(day.tasks().getNames()).containsExactly("Task, project", "Task, area");
        assertThat(day.timeEvents().getNames()).containsExactly("Event, project", "Event, area");
        assertThat(day.dayEvents().getNames()).containsExactly("Day event", "Second day event");

        day.filter().project("Business idea");

        day.assertProjectFilter("Business idea");
        assertThat(day.tasks().getNames()).containsExactly("Task, project");
        assertThat(day.timeEvents().getNames()).containsExactly("Event, project");
        assertThat(day.dayEvents().getNames()).containsExactly("Day event", "Second day event");

        day.filter().none();

        day.assertNoFilter();
        assertThat(day.tasks().getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(day.timeEvents().getNames()).containsExactly("Event", "Event, project", "Event, other project", "Event, area", "Event, other area");
        assertThat(day.dayEvents().getNames()).containsExactly("Day event", "Second day event");
    }

    @BeforeAll
    public static void beforeAll() {
        system.createDayTask("Task", date);
        system.createDayTaskWithProject("Task, project", date, 0, "Business idea");
        system.createDayTaskWithProject("Task, other project", date, 0, "Spanish");
        system.createDayTaskWithArea("Task, area", date, "Business");
        system.createDayTaskWithArea("Task, other area", date, "Self-improvement");

        system.createDayTimeEvent("Event", time(date, 10), time(date, 11));
        system.createDayTimeEventWithProject("Event, project", time(date, 12), time(date, 13), "Business idea");
        system.createDayTimeEventWithProject("Event, other project", time(date, 14), time(date, 15), "Spanish");
        system.createDayTimeEventWithArea("Event, area", time(date, 16), time(date, 17), "Business");
        system.createDayTimeEventWithArea("Event, other area", time(date, 18), time(date, 19), "Self-improvement");

        system.createDayEvent("Day event", date, date);
        system.createDayEvent("Second day event", date, date);

        dayCaptain.initWithLogin();
    }

    @AfterAll
    public static void afterAll() {
        dayCaptain.close();

        system.deleteDayTasks(date);
        system.deleteDayTimeEvents(date);
        system.deleteDayEvents(date);
    }


}
