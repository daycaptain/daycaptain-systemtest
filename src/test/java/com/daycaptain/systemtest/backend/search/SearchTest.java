package com.daycaptain.systemtest.backend.search;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import com.daycaptain.systemtest.backend.entity.SearchResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchTest {

    private static final YearWeek WEEK = YearWeek.of(2021, 2);
    private static final LocalDate DATE = WEEK.atDay(DayOfWeek.MONDAY);

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @BeforeEach
    void setUp() {
        dayCaptain.createInboxItem("Task test");
        URI id = dayCaptain.createInboxItem("Task test archived");
        BacklogItem backlogItem = dayCaptain.getBacklogItem(id);
        dayCaptain.updateBacklogItem(backlogItem, "status", "DONE");
        dayCaptain.updateBacklogItem(backlogItem, "archived", true);

        dayCaptain.createWeekTask("Week task 1", WEEK);
        dayCaptain.createWeekTask("Week 2", WEEK);
        dayCaptain.createDayTask("Day task 1", DATE);
        dayCaptain.createDayTask("task 2", DATE);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteWeekTasks(WEEK);
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteBacklogItemsInAllBacklogs("Task test", "Task test archived");
    }

    @Test
    void search_returns_items() {
        SearchResult result = dayCaptain.search("task");
        assertThat(result.backlogItems).extracting(i -> i.string)
                .contains("Task test")
                .doesNotContain("Task test archived");
        assertThat(result.dayTasks).extracting(i -> i.string).contains("Day task 1", "task 2");
        assertThat(result.weekTasks).extracting(i -> i.string).contains("Week task 1");
    }

    @Test
    void empty_search() {
        SearchResult result = dayCaptain.search("");
        assertThat(List.of(
                result.backlogItems,
                result.dayEvents,
                result.timeEvents,
                result.dayTasks,
                result.weekTasks,
                result.projects
        )).allMatch(List::isEmpty);
    }

}
