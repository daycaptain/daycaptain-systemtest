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

import static org.assertj.core.api.Assertions.assertThat;

// created since SearchRelationsTests needs to be fixed
class SearchBacklogItemRelationsTest {

    private static final YearWeek WEEK = YearWeek.of(2021, 2);
    private static final LocalDate DATE = WEEK.atDay(DayOfWeek.MONDAY);

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @BeforeEach
    void setUp() {
        dayCaptain.createInboxItem("Task relation test");
        URI id = dayCaptain.createInboxItem("Task relation test archived");
        BacklogItem backlogItem = dayCaptain.getBacklogItem(id);
        dayCaptain.updateBacklogItem(backlogItem, "status", "DONE");
        dayCaptain.updateBacklogItem(backlogItem, "archived", true);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteWeekTasks(WEEK);
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteBacklogItemsInAllBacklogs("Task relation test", "Task relation test archived");
    }

    @Test
    void search_week_task_ignore_archived_backlog_item() {
        URI taskId = dayCaptain.createWeekTask("Week task", WEEK);
        dayCaptain.createDayTask("Day task", DATE);
        SearchResult result = dayCaptain.searchPotentialRelations(taskId, "");

        assertThat(result.dayEvents).isEmpty();
        assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Day task");
        assertThat(result.weekTasks).isEmpty();
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string)
                .contains("Task relation test")
                .doesNotContain("Task relation test archived");
    }

    @Test
    void search_week_task_ignore_archived_backlog_item_search_term() {
        URI taskId = dayCaptain.createWeekTask("Week task", WEEK);
        dayCaptain.createDayTask("Day task", DATE);
        SearchResult result = dayCaptain.searchPotentialRelations(taskId, "task");

        assertThat(result.dayEvents).isEmpty();
        assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Day task");
        assertThat(result.weekTasks).isEmpty();
        assertThat(result.projects).isEmpty();
        assertThat(result.backlogItems).extracting(t -> t.string)
                .contains("Task relation test")
                .doesNotContain("Task relation test archived");
    }

    @Test
    void search_day_task_ignore_archived_backlog_item() {
        URI taskId = dayCaptain.createDayTask("Day task", DATE);
        dayCaptain.createWeekTask("Week task", WEEK);
        SearchResult result = dayCaptain.searchPotentialRelations(taskId, "");

        assertThat(result.dayEvents).isEmpty();
        assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Week task");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string)
                .contains("Task relation test")
                .doesNotContain("Task relation test archived");
    }

    @Test
    void search_day_task_ignore_archived_backlog_item_search_term() {
        URI taskId = dayCaptain.createDayTask("Day task", DATE);
        dayCaptain.createWeekTask("Week task", WEEK);
        SearchResult result = dayCaptain.searchPotentialRelations(taskId, "task");

        assertThat(result.dayEvents).isEmpty();
        assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Week task");
        assertThat(result.projects).isEmpty();
        assertThat(result.backlogItems).extracting(t -> t.string)
                .contains("Task relation test")
                .doesNotContain("Task relation test archived");
    }

}
