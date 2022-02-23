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

        dayCaptain.createDayTask("Ttask ddone", DATE);
        dayCaptain.createDayTask("Ttask \"ddone\"", DATE);
        dayCaptain.createDayTask("Ttask AND ddone", DATE);
        dayCaptain.createDayTask("Ttas ddone", DATE);
        dayCaptain.createDayTask("Ttask OR ddone", DATE);
        dayCaptain.createDayTask("Ttas' ddone", DATE);
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
        assertThat(result.backlogItems).extracting("string")
                .contains("Task test")
                .doesNotContain("Task test archived");
        assertThat(result.dayTasks).extracting("string").contains("Day task 1", "task 2");
        assertThat(result.weekTasks).extracting("string").contains("Week task 1");
    }

    @Test
    void search_special_cases() {
        SearchResult result = dayCaptain.search("ttask");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask ddone", "Ttask \"ddone\"", "Ttask AND ddone", "Ttask OR ddone");

        result = dayCaptain.search("ttask ddone");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask ddone", "Ttask \"ddone\"", "Ttask AND ddone", "Ttask OR ddone");

        result = dayCaptain.search("ttas ddone");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask ddone", "Ttask \"ddone\"", "Ttask AND ddone", "Ttas ddone", "Ttask OR ddone", "Ttas' ddone");

        result = dayCaptain.search("ttask \"ddone\"");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask ddone", "Ttask \"ddone\"", "Ttask AND ddone", "Ttask OR ddone");

        result = dayCaptain.search("ttask AND ddone");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask AND ddone");

        result = dayCaptain.search("ttask OR ddone");
        assertThat(result.dayTasks).extracting("string").containsExactly("Ttask OR ddone");
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
