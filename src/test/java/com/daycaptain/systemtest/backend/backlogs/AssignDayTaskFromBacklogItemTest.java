package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignDayTaskFromBacklogItemTest {

    private static final LocalDate DATE = LocalDate.of(2020, 5, 13);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void create_task_from_backlog_item() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("New backlog item");

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.string).isEqualTo("New day task");
        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
    }

    @Test
    void add_relation_existing_task_from_backlog_item() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("New backlog item");

        URI taskId = dayCaptain.createDayTask("New day task", DATE);
        Task task = dayCaptain.getTask(taskId);
        assertThat(task.assignedFromBacklogTask).isNull();

        dayCaptain.addRelation(item._self, task._self);
        task = dayCaptain.getTask(taskId);
        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
    }

    @Test
    void reassign_task_should_remove_other_link_to_task() {
        URI firstItem = dayCaptain.createInboxItem("New backlog item");
        URI secondItem = dayCaptain.createInboxItem("Another backlog item");
        URI task = dayCaptain.createDayTask("New day task", DATE);

        dayCaptain.addRelation(firstItem, task);
        assertThat(dayCaptain.getBacklogItem(firstItem).assignedTasks).containsExactly(task);
        assertThat(dayCaptain.getTask(task).assignedFromBacklogTask).isEqualTo(firstItem);

        dayCaptain.addRelation(secondItem, task);
        assertThat(dayCaptain.getBacklogItem(secondItem).assignedTasks).containsExactly(task);
        assertThat(dayCaptain.getBacklogItem(firstItem).assignedTasks).isEmpty();
        assertThat(dayCaptain.getTask(task).assignedFromBacklogTask).isEqualTo(secondItem);
    }

    @Test
    void remove_relation_removes_link_to_task() {
        URI item = dayCaptain.createInboxItem("New backlog item");
        URI task = dayCaptain.createDayTask("New day task", DATE);

        dayCaptain.addRelation(item, task);
        assertThat(dayCaptain.getBacklogItem(item).assignedTasks).containsExactly(task);
        assertThat(dayCaptain.getTask(task).assignedFromBacklogTask).isEqualTo(item);

        dayCaptain.removeRelation(item, task);
        assertThat(dayCaptain.getBacklogItem(item).assignedTasks).isEmpty();
        assertThat(dayCaptain.getTask(task).assignedFromBacklogTask).isNull();
    }

    @Test
    void create_task_from_backlog_item_related_area() {
        URI itemId = dayCaptain.createInboxItemWithArea("New backlog item", "IT work");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
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

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
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

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
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

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void create_task_from_archived_backlog_item_related_backlog_area() {
        URI backlogId = dayCaptain.createBacklogWithArea("New backlog", "IT work");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        dayCaptain.updateBacklogItem(item, "status", "DONE");
        dayCaptain.updateBacklogItem(item, "archived", true);

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("IT work");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    void create_task_from_archived_backlog_item_related_backlog_project() {
        URI backlogId = dayCaptain.createBacklogWithProject("New backlog", "Business idea");
        Backlog backlog = dayCaptain.getBacklog(backlogId);
        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        dayCaptain.updateBacklogItem(item, "status", "DONE");
        dayCaptain.updateBacklogItem(item, "archived", true);

        URI taskId = dayCaptain.createDayTask("New day task", DATE, item);
        Task task = dayCaptain.getTask(taskId);

        assertThat(task.assignedFromBacklogTask).isEqualTo(item._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteBacklogItemsInAllBacklogs("New backlog item", "Another backlog item");
        dayCaptain.deleteBacklogs("New backlog");
    }

}
