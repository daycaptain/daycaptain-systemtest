package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditDayEventAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;

public class DayDeepLinkUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 18);

    @Test
    void day_task_deep_link_selected() {
        system.createDayTask("Day task 1", date);
        URI uri = system.createDayTask("Day task 2", date);
        assertThat(dayCaptain.day(date.minusDays(1)).getDateHeader()).contains("17th February 2021");

        DayView dayView = dayCaptain.dayLink(uri);
        EditTaskAction edit = dayView.focusedTaskList().edit();
        assertThat(edit.getName()).isEqualTo("Day task 2");
    }

    @Test
    void day_time_event_deep_link_selected() {
        system.createDayTimeEvent("Time event 1", time(date, 10), time(date, 11));
        URI uri = system.createDayTimeEvent("Time event 2", time(date, 12), time(date, 14));
        assertThat(dayCaptain.day(date.minusDays(1)).getDateHeader()).contains("17th February 2021");

        DayView dayView = dayCaptain.dayLink(uri);
        EditTimeEventAction edit = dayView.focusedTimeEvents().edit();
        assertThat(edit.getName()).isEqualTo("Time event 2");
    }

    @Test
    void day_event_deep_link_selected() {
        system.createDayEvent("Day event 1", date, date);
        URI uri = system.createDayEvent("Day event 2", date, date);
        assertThat(dayCaptain.day(date.minusDays(1)).getDateHeader()).contains("17th February 2021");

        DayView dayView = dayCaptain.dayLink(uri);
        EditDayEventAction edit = dayView.focusedDayEvents().edit();
        assertThat(edit.getName()).isEqualTo("Day event 2");
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTasks(date);
        system.deleteDayTimeEvents(date);
        system.deleteDayEvents(date);
    }

    @BeforeAll
    public static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    public static void afterAll() {
        dayCaptain.close();
    }

}
