package com.daycaptain.systemtest.backend.search;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import com.daycaptain.systemtest.backend.entity.SearchResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.daycaptain.systemtest.Times.time;
import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklog;
import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklogItem;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchAdvancedTest {

    private static final YearWeek WEEK = YearWeek.of(2021, 2);
    private static final LocalDate DATE = LocalDate.of(2021, 1, 11);
    private static final LocalDate DATE_2 = LocalDate.of(2020, 1, 11);
    private static final YearWeek WEEK_2 = YearWeek.of(2020, 2);

    private static final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void search_returns_items_full_information() {
        SearchResult result = dayCaptain.searchAdvanced("taskk", null, null);
        assertThat(result.backlogItems).extracting("string").containsExactlyInAnyOrder(
                "Taskk test",
                "Taskk test with IT area",
                "Taskk test with Business area",
                "Taskk test with Business project",
                "Taskk test with Spanish project",
                "Taskk in backlog",
                "Taskk in backlog with IT area");

        assertThat(findBacklogItem(result.backlogItems, "Taskk test with IT area").relatedArea).isEqualTo("IT work");
        assertThat(findBacklogItem(result.backlogItems, "Taskk test with Business area").area).isEqualTo("Business");
        assertThat(findBacklogItem(result.backlogItems, "Taskk test with Business project"))
                .extracting(t -> t.relatedArea, t -> t.project, t -> t.backlogName).containsExactly("Business", "Business idea", null);
        assertThat(findBacklogItem(result.backlogItems, "Taskk test with Spanish project"))
                .extracting(t -> t.relatedArea, t -> t.project, t -> t.backlogName).containsExactly(null, "Spanish", null);
        assertThat(findBacklogItem(result.backlogItems, "Taskk in backlog"))
                .extracting(t -> t.relatedArea, t -> t.backlogName).containsExactly(null, "To-contact");
        assertThat(findBacklogItem(result.backlogItems, "Taskk in backlog with IT area"))
                .extracting(t -> t.relatedArea, t -> t.backlogName).containsExactly("IT work", "To-contact");

        assertThat(result.dayTasks).extracting("string").containsExactlyInAnyOrder("Day taskk 1", "taskk 2", "Day taskk 3");
        assertThat(result.weekTasks).extracting("string").containsExactlyInAnyOrder("Week taskk 1", "Week taskk 3");

        assertThat(result.dayEvents).isEmpty();
        assertThat(result.projects).isEmpty();
        assertThat(result.timeEvents).extracting("string").containsExactlyInAnyOrder("Eevent taskk 1", "Eevent taskk 2");
    }

    @Test
    void search_all_for_area() {
        SearchResult result = dayCaptain.searchAdvanced("", "IT work", null);
        assertThat(List.of(
                result.dayEvents,
                result.timeEvents,
                result.dayTasks,
                result.weekTasks
        )).allMatch(List::isEmpty);

        assertThat(result.backlogItems).extracting("string")
                .contains("Taskk test with IT area")
                .doesNotContain("Taskk test", "Taskk test with Business area");
        assertThat(result.projects).extracting("string")
                .contains("Work presentations")
                .doesNotContain("Spanish");
    }

    @Test
    void search_all_for_project() {
        SearchResult result = dayCaptain.searchAdvanced("", null, "Business idea");

        assertThat(result.projects).extracting("string").isEmpty();
        assertThat(result.dayEvents).extracting("string").doesNotContain("Taskk");
        assertThat(result.timeEvents).extracting("string").doesNotContain("Taskk");
        assertThat(result.dayTasks).extracting("string").doesNotContain("Taskk");
        assertThat(result.weekTasks).extracting("string").doesNotContain("Taskk");

        assertThat(result.backlogItems).extracting("string")
                .contains("Taskk test with Business project")
                .doesNotContain("Taskk test", "Taskk test with Spanish project");
    }

    @Test
    void search_date_range() {
        LocalDate from = LocalDate.of(2020, 1, 1);
        LocalDate to = LocalDate.of(2020, 12, 31);

        SearchResult result = dayCaptain.searchAdvanced("taskk", null, null, from, to);

        assertThat(result.dayTasks).extracting("string").containsExactly("Day taskk 3");
        assertThat(result.timeEvents).extracting("string").containsExactly("Eevent taskk 2");
        assertThat(result.weekTasks).extracting("string").containsExactly("Week taskk 3");

        from = LocalDate.of(2021, 1, 1);
        to = LocalDate.of(2021, 12, 31);

        result = dayCaptain.searchAdvanced("taskk", null, null, from, to);
        assertThat(result.dayTasks).extracting("string").containsExactly("Day taskk 1", "taskk 2");
        assertThat(result.timeEvents).extracting("string").containsExactly("Eevent taskk 1");
        assertThat(result.weekTasks).extracting("string").containsExactly("Week taskk 1");
    }

    @Test
    void search_date_range_week_fits_in_dates() {
        LocalDate from = LocalDate.of(2020, 1, 5);
        LocalDate to = LocalDate.of(2020, 1, 13);

        SearchResult result = dayCaptain.searchAdvanced("taskk", null, null, from, to);

        assertThat(result.weekTasks).extracting("string").containsExactly("Week taskk 3");

        from = LocalDate.of(2020, 1, 13);
        to = LocalDate.of(2020, 1, 19);
        result = dayCaptain.searchAdvanced("taskk", null, null, from, to);
        assertThat(result.weekTasks).extracting("string").isEmpty();
    }

    @Test
    void search_date_range_first_year_week_matches_year_dates() {
        URI uri = dayCaptain.createWeekTask("SearchAdvancedTest 1", YearWeek.of(2022, 1));
        URI uri2 = dayCaptain.createWeekTask("SearchAdvancedTest 2", YearWeek.of(2021, 52));

        LocalDate from = LocalDate.of(2022, 1, 1);
        LocalDate to = LocalDate.of(2022, 12, 31);

        SearchResult result = dayCaptain.searchAdvanced("SearchAdvancedTest", null, null, from, to);
        assertThat(result.weekTasks).extracting("string").containsExactly("SearchAdvancedTest 1");

        from = LocalDate.of(2021, 1, 1);
        to = LocalDate.of(2021, 12, 31);
        result = dayCaptain.searchAdvanced("SearchAdvancedTest", null, null, from, to);
        assertThat(result.weekTasks).extracting("string").containsExactly("SearchAdvancedTest 2");

        dayCaptain.deleteTask(dayCaptain.getTask(uri));
        dayCaptain.deleteTask(dayCaptain.getTask(uri2));
    }

    @Test
    void search_date_range_from_to_inclusive() {
        LocalDate from = LocalDate.of(2020, 1, 11);
        LocalDate to = LocalDate.of(2020, 1, 13);

        SearchResult result = dayCaptain.searchAdvanced("taskk", null, null, from, to);

        assertThat(result.dayTasks).extracting("string").containsExactly("Day taskk 3");
        assertThat(result.timeEvents).extracting("string").containsExactly("Eevent taskk 2");
        assertThat(result.weekTasks).extracting("string").containsExactly("Week taskk 3");

        from = LocalDate.of(2020, 1, 10);
        to = LocalDate.of(2020, 1, 11);

        result = dayCaptain.searchAdvanced("taskk", null, null, from, to);
        assertThat(result.dayTasks).extracting("string").containsExactly("Day taskk 3");
        assertThat(result.timeEvents).extracting("string").containsExactly("Eevent taskk 2");
        assertThat(result.weekTasks).extracting("string").containsExactly("Week taskk 3");
    }

    @Test
    void search_regex() {
        SearchResult result = dayCaptain.searchAdvancedRegex("Taskk test", null, null);
        assertThat(result.dayTasks).extracting("string").isEmpty();
        assertThat(result.timeEvents).extracting("string").isEmpty();
        assertThat(result.weekTasks).extracting("string").isEmpty();
        assertThat(result.backlogItems).extracting("string").containsExactly("Taskk test");
    }

    @Test
    void search_regex_char_range() {
        SearchResult result = dayCaptain.searchAdvancedRegex("Taskk [a-z]{4}", null, null);
        assertThat(result.dayTasks).extracting("string").isEmpty();
        assertThat(result.timeEvents).extracting("string").isEmpty();
        assertThat(result.weekTasks).extracting("string").isEmpty();
        assertThat(result.backlogItems).extracting("string").containsExactly("Taskk test");
    }

    @Test
    void search_regex_wildcard() {
        SearchResult result = dayCaptain.searchAdvancedRegex("Taskk .*", null, null);
        assertThat(result.dayTasks).extracting("string").isEmpty();
        assertThat(result.timeEvents).extracting("string").isEmpty();
        assertThat(result.weekTasks).extracting("string").isEmpty();
        assertThat(result.backlogItems).extracting("string").containsExactly("Taskk test", "Taskk test with IT area", "Taskk test with Business area", "Taskk test with Business project", "Taskk test with Spanish project", "Taskk in backlog", "Taskk in backlog with IT area");
    }

    @Test
    void search_regex_illegal_chars() {
        SearchResult result = dayCaptain.searchAdvancedRegex("Taskk$$", null, null);
        assertThat(result.dayTasks).extracting("string").isEmpty();
        assertThat(result.timeEvents).extracting("string").isEmpty();
        assertThat(result.weekTasks).extracting("string").isEmpty();
        assertThat(result.backlogItems).extracting("string").isEmpty();
    }

    @BeforeAll
    static void setUp() {
        dayCaptain.createInboxItem("Taskk test");
        dayCaptain.createInboxItemWithArea("Taskk test with IT area", "IT work");
        dayCaptain.createInboxItemWithArea("Taskk test with Business area", "Business");
        dayCaptain.createInboxItemWithProject("Taskk test with Business project", "Business idea");
        dayCaptain.createInboxItemWithProject("Taskk test with Spanish project", "Spanish");
        URI id = dayCaptain.createInboxItem("Taskk test archived");
        BacklogItem backlogItem = dayCaptain.getBacklogItem(id);
        dayCaptain.updateBacklogItem(backlogItem, "status", "DONE");
        dayCaptain.updateBacklogItem(backlogItem, "archived", true);

        Backlog backlog = findBacklog(dayCaptain.getBacklogs(), "To-contact");
        dayCaptain.createBacklogItem("Taskk in backlog", backlog);
        dayCaptain.createBacklogItemWithArea("Taskk in backlog with IT area", backlog, "IT work");

        dayCaptain.createWeekTask("Week taskk 1", WEEK);
        dayCaptain.createWeekTask("Week 2", WEEK);
        dayCaptain.createWeekTask("Week taskk 3", WEEK_2);

        dayCaptain.createDayTask("Day taskk 1", DATE);
        dayCaptain.createDayTask("taskk 2", DATE);
        dayCaptain.createDayTask("Day taskk 3", DATE_2);

        dayCaptain.createDayTimeEvent("Eevent taskk 1", time(DATE, 10), time(DATE, 11));
        dayCaptain.createDayTimeEvent("Eevent taskk 2", time(DATE_2, 10), time(DATE_2, 11));
    }

    @AfterAll
    static void tearDown() {
        dayCaptain.deleteWeekTasks(WEEK);
        dayCaptain.deleteWeekTasks(WEEK_2);
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteDayTasks(DATE_2);
        dayCaptain.deleteDayTimeEvents(DATE, DATE_2);
        dayCaptain.deleteBacklogItemsInAllBacklogs(
                "Taskk test",
                "Taskk test archived",
                "Taskk test with IT area",
                "Taskk test with Business area",
                "Taskk test with Business project",
                "Taskk test with Spanish project",
                "Taskk in backlog",
                "Taskk in backlog with IT area"
        );
    }

}
