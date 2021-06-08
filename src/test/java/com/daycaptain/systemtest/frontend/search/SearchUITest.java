package com.daycaptain.systemtest.frontend.search;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.SearchAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.entity.Task;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final YearWeek week = YearWeek.of(2021, 12);

    @Test
    void day_view_search_jump_to_day_task() {
        SearchAction search = dayCaptain.day().search();
        search.searchTerm("taskk");
        assertThat(search.getResults()).containsExactly("Dayy taskk", "Weekk taskk");
        search.gotoSelection();
        Task task = new DayView().focusedTaskList().focused();
        assertThat(task.string).isEqualTo("Dayy taskk");
    }

    @Test
    void day_view_search_jump_to_time_event() {
        SearchAction search = dayCaptain.day().search();
        search.searchTerm("timee");
        assertThat(search.getResults()).containsExactly("Timee eventt");
        search.gotoSelection();
        ListItem event = new DayView().focusedTimeEvents().focused();
        assertThat(event.string).isEqualTo("Timee eventt");
    }

    @Test
    void day_view_search_jump_to_day_event() {
        throw new UnsupportedOperationException();
    }

    @Test
    void day_view_search_jump_to_week_task() {
        throw new UnsupportedOperationException();
    }

    @Test
    void day_view_search_jump_to_project() {
        throw new UnsupportedOperationException();
    }

    @Test
    void day_view_search_jump_to_inbox_item() {
        throw new UnsupportedOperationException();
    }

    @Test
    void day_view_search_jump_to_backlog_item() {
        throw new UnsupportedOperationException();
    }

    @Test
    void week_view_search_jump_to_day_task() {
        throw new UnsupportedOperationException();
    }

    @Test
    void month_view_search_jump_to_day_task() {
        throw new UnsupportedOperationException();
    }

    @Test
    void backlogs_view_search_jump_to_day_task() {
        throw new UnsupportedOperationException();
    }

    @BeforeAll
    public static void beforeAll() {
        // avoid naming collision with other system tests
        system.createDayTask("Dayy taskk", date);
        system.createDayTimeEvent("Timee eventt", time(date, 11), time(date, 13));
        system.createDayEvent("Dayy eventt", date, date.plusDays(1));
        system.createWeekTask("Weekk taskk", week);
        system.createInboxItem("Inboxx itemm");
        system.createProject("Projectt 1");
        dayCaptain.initWithLogin();
    }

    @AfterAll
    public static void afterAll() {
        system.deleteDayTasks(date);
        system.deleteDayTimeEvents(date);
        system.deleteDayEvents(date);
        system.deleteWeekTasks(week);
        system.deleteBacklogItemsInAllBacklogs("Inboxx itemm");
        system.deleteProjects("Projectt 1");
        dayCaptain.close();
    }

}
