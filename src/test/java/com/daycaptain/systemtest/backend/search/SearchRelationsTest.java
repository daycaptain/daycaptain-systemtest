package com.daycaptain.systemtest.backend.search;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;

import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklogItem;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class SearchRelationsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testSearchRelationsBacklogItemsBacklogLinksMissing() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        Task task = CollectionUtils.findTask(dayCaptain.getDay(date).tasks, "Reading");

        URI inbox = dayCaptain.getInbox()._self;
        URI backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "To-contact")._self;

        SearchResult result = dayCaptain.searchPotentialRelations(task._self, "");
        // deliberately ignored, since links would add little value here
        assertThat(findBacklogItem(result.backlogItems, "Clean up").backlog).isNull();
        assertThat(findBacklogItem(result.backlogItems, "Contact designer").backlog).isNull();
    }

    @Test
    void testSearchPotentialRelationsWeekTask() {
        YearWeek week = YearWeek.of(2020, 19);
        Task task = CollectionUtils.findTask(dayCaptain.getWeek(week).tasks, "Something");

        SearchResult result = dayCaptain.searchPotentialRelations(task._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).extracting(t -> t.string).contains("Working on my project", "Reading");
        assertThat(result.dayTasks).extracting(t -> t.string).contains("Reading", "Working on my project", "Plan business rework", "Plan business rework", "Rework business idea");
        assertThat(result.weekTasks).isEmpty();
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string).contains("Call mum", "Clean up", "Contact designer", "Contact tax consultant", "Doing taxes", "Plan business rework", "Plan new IT work", "Rework business idea");
    }

    @Test
    void testSearchPotentialRelationsWeekTaskSearchTerm() {
        YearWeek week = YearWeek.of(2020, 19);
        Task task = CollectionUtils.findTask(dayCaptain.getWeek(week).tasks, "Something");

        SearchResult result = dayCaptain.searchPotentialRelations(task._self, "business");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Plan business rework", "Rework business idea");
        assertThat(result.weekTasks).isEmpty();
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea");
        assertThat(result.backlogItems).extracting(t -> t.string).contains("Rework business idea");
    }

    @Test
    void testSearchPotentialRelationsDayTask() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        Task task = CollectionUtils.findTask(dayCaptain.getDay(date).tasks, "Reading");

        SearchResult result = dayCaptain.searchPotentialRelations(task._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).extracting(t -> t.string).contains("Reading");
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).contains("Plan business rework", "Rework business idea", "Something", "Working on my project");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string).contains("Clean up", "Contact designer", "Plan business rework", "Plan new IT work", "Doing taxes", "Contact tax consultant", "Call mum", "Rework business idea");
    }

    @Test
    @Disabled
    void testSearchPotentialRelationsDayTaskSearchTerm() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        Task task = CollectionUtils.findTask(dayCaptain.getDay(date).tasks, "Reading");

        SearchResult result = dayCaptain.searchPotentialRelations(task._self, "business");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Past business idea");
        assertThat(result.backlogItems).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea");
    }

    @Test
    @Disabled
    void testSearchDayTimeEventRelations() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, "Reading");

        SearchResult result = dayCaptain.searchPotentialRelations(event._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Reading");
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea", "Something", "Working on my project");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Past business idea", "Past presentations", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string).containsExactly("Clean up", "Contact designer", "Plan business rework", "Plan new IT work", "Doing taxes", "Contact tax consultant", "Call mum", "Rework business idea");
    }

    @Test
    @Disabled
    void testSearchDayTimeEventRelationsQuery() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, "Reading");

        SearchResult result = dayCaptain.searchPotentialRelations(event._self, "business");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Past business idea");
        assertThat(result.backlogItems).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea");
    }

    @Test
    @Disabled
    void testSearchDayTimeEventRelationsRelatedToDayTask() {
        LocalDate date = LocalDate.of(2020, 5, 8);
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, "Working on my project");

        SearchResult result = dayCaptain.searchPotentialRelations(event._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).isEmpty();
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea", "Something");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Past business idea", "Past presentations", "Spanish", "Work presentations");
        assertThat(result.backlogItems).extracting(t -> t.string).containsExactly("Clean up", "Contact designer", "Plan business rework", "Plan new IT work", "Doing taxes", "Contact tax consultant", "Call mum", "Rework business idea");
    }

    @Test
    @Disabled
    void testSearchPotentialRelationsBacklogItem() {
        Backlog inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX");
        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox._self).items, "Clean up");

        SearchResult result = dayCaptain.searchPotentialRelations(item._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Plan business rework", "Reading", "Rework business idea", "Working on my project");
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea", "Working on my project", "Something");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Past presentations", "Spanish", "Past business idea", "Work presentations");
        assertThat(result.backlogItems).isEmpty();
    }

    @Test
    @Disabled
    void testSearchPotentialRelationsBacklogItemSearchTerm() {
        Backlog inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX");
        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox._self).items, "Clean up");

        SearchResult result = dayCaptain.searchPotentialRelations(item._self, "business");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Plan business rework", "Rework business idea");
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Plan business rework", "Rework business idea");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Business idea", "Past business idea");
        assertThat(result.backlogItems).isEmpty();
    }

    @Test
    @Disabled
    void testSearchPotentialRelationsBacklogItemRelatedToTasks() {
        Backlog inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX");
        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox._self).items, "Plan business rework");

        SearchResult result = dayCaptain.searchPotentialRelations(item._self, "");

        assertThat(result.dayEvents).isEmpty();
        Assertions.assertThat(result.timeEvents).isEmpty();
        assertThat(result.dayTasks).extracting(t -> t.string).containsExactly("Reading", "Rework business idea", "Working on my project");
        assertThat(result.weekTasks).extracting(t -> t.string).containsExactly("Rework business idea", "Something", "Working on my project");
        assertThat(result.projects).extracting(t -> t.string).containsExactly("Past presentations", "Spanish", "Past business idea", "Work presentations");
        assertThat(result.backlogItems).isEmpty();
    }

}
