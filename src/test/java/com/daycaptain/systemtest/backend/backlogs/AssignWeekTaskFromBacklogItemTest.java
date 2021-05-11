package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignWeekTaskFromBacklogItemTest {

    private static final YearWeek WEEK = YearWeek.of(2020, 18);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void create_task_from_backlog_item() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("New backlog item");

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.string).isEqualTo("New week task");
        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
    }

    @Test
    void add_relation_existing_task_from_backlog_item() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("New backlog item");

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK);
        Task task = dayCaptain.getTask(taskId);
        assertThat(task.assignedFromBacklogTask).isNull();

        dayCaptain.addRelation(task, item._self);
        task = dayCaptain.getTask(taskId);
        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
    }

    @Test
    void create_task_from_backlog_item_related_area() {
        URI itemId = dayCaptain.createInboxItemWithArea("New backlog item", "IT work");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("IT work");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    void create_task_from_backlog_item_related_project() {
        URI itemId = dayCaptain.createInboxItemWithProject("New backlog item", "Business idea");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void create_task_from_backlog_item_related_backlog_area() {
        URI backlogId = dayCaptain.createBacklogWithArea("New backlog", "IT work");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("IT work");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    void create_task_from_backlog_item_related_backlog_project() {
        URI backlogId = dayCaptain.createBacklogWithProject("New backlog", "Business idea");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void inherit_area_from_backlog_item_to_week_day_event() {
        URI itemId = dayCaptain.createInboxItemWithArea("New backlog item", "IT work");

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task weekTask = dayCaptain.getTask(taskId);

        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
        Task dayTask = dayCaptain.getTask(taskId);
        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);

        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("IT work");
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isNull();
    }

    @Test
    void inherit_project_from_backlog_item_to_week_day_event() {
        URI itemId = dayCaptain.createInboxItemWithProject("New backlog item", "Business idea");

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task weekTask = dayCaptain.getTask(taskId);

        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
        Task dayTask = dayCaptain.getTask(taskId);
        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);

        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("Business");
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void inherit_area_from_backlog_to_week_day_event() {
        URI backlogId = dayCaptain.createBacklogWithArea("New backlog", "IT work");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task weekTask = dayCaptain.getTask(taskId);

        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
        Task dayTask = dayCaptain.getTask(taskId);
        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);

        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("IT work");
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isNull();
    }

    @Test
    void inherit_project_from_backlog_to_week_day_event() {
        URI backlogId = dayCaptain.createBacklogWithProject("New backlog", "Business idea");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
        Task weekTask = dayCaptain.getTask(taskId);

        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
        Task dayTask = dayCaptain.getTask(taskId);
        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);

        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("Business");
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

}
