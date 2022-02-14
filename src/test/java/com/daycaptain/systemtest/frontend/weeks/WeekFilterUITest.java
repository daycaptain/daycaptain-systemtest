package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static java.time.DayOfWeek.SUNDAY;
import static org.assertj.core.api.Assertions.assertThat;

class WeekFilterUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final YearWeek week = YearWeek.of(2022, 6);
    private static final LocalDate date = LocalDate.of(2022, 2, 13);

    @Test
    void filter_tasks_time_events_header_shown() {
        WeekView week = dayCaptain.week(WeekFilterUITest.week);

        week.assertNoFilter();
        assertThat(week.weekTasks().getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(week.dayTasks(SUNDAY).getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(week.dayTimeEventsOffset(0).getNames()).containsExactly("Event", "Event, project", "Event, other project", "Event, area", "Event, other area");
        assertThat(week.dayEventsOffset(0).getNames()).containsExactly("Day event", "Second day event");

        week.filter().area("Business");

        week.assertAreaFilter("Business");
        assertThat(week.weekTasks().getNames()).containsExactly("Task, project", "Task, area");
        assertThat(week.dayTasksOffset(0).getNames()).containsExactly("Task, project", "Task, area");
        assertThat(week.dayTimeEventsOffset(0).getNames()).containsExactly("Event, project", "Event, area");
        assertThat(week.dayEventsOffset(0).getNames()).containsExactly("Day event", "Second day event");

        week.filter().project("Business idea");

        week.assertProjectFilter("Business idea");
        assertThat(week.weekTasks().getNames()).containsExactly("Task, project");
        assertThat(week.dayTasksOffset(0).getNames()).containsExactly("Task, project");
        assertThat(week.dayTimeEventsOffset(0).getNames()).containsExactly("Event, project");
        assertThat(week.dayEventsOffset(0).getNames()).containsExactly("Day event", "Second day event");

        week.filter().none();

        week.assertNoFilter();
        assertThat(week.weekTasks().getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(week.dayTasksOffset(0).getNames()).containsExactly("Task", "Task, project", "Task, other project", "Task, area", "Task, other area");
        assertThat(week.dayTimeEventsOffset(0).getNames()).containsExactly("Event", "Event, project", "Event, other project", "Event, area", "Event, other area");
        assertThat(week.dayEventsOffset(0).getNames()).containsExactly("Day event", "Second day event");
    }

    @BeforeAll
    public static void beforeAll() {
        system.createWeekTask("Task", week);
        system.createWeekTaskWithProject("Task, project", week, 0, "Business idea");
        system.createWeekTaskWithProject("Task, other project", week, 0, "Spanish");
        system.createWeekTaskWithArea("Task, area", week, 0, "Business");
        system.createWeekTaskWithArea("Task, other area", week, 0, "Self-improvement");

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

        system.deleteWeekTasks(week);
        system.deleteDayTasks(date);
        system.deleteDayTimeEvents(date);
        system.deleteDayEvents(date);
    }


}
