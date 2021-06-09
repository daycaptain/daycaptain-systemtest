package com.daycaptain.systemtest.frontend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.views.BacklogsView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.time.format.TextStyle.SHORT;
import static java.util.Locale.US;
import static org.assertj.core.api.Assertions.assertThat;

public class AssignDayTaskFromBacklogItemUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final LocalDate DATE = LocalDate.now();
    private static final String DATE_STRING_1 = DATE.getDayOfWeek().getDisplayName(SHORT, US) + ", " + DATE.getDayOfMonth();
    private static final String DATE_STRING_2 = DATE.getMonth().getDisplayName(SHORT, US);

    @Test
    void create_task_from_backlog_item() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItem("New backlog item");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New backlog item");

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList dayTasks = dayCaptain.day().tasks();
        assertThat(dayTasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = dayTasks.editLast();
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New backlog item").contains("INBOX");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_backlog() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createBacklogItem("New contact item", "To-contact");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New contact item");

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList tasks = dayCaptain.day().tasks();
        assertThat(tasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = tasks.editLast();
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New contact item").contains("To-contact");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_related_area() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItemWithArea("New backlog item", "i");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.string).isEqualTo("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList tasks = dayCaptain.day().tasks();
        assertThat(tasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = tasks.editLast();
        assertThat(editTask.getArea()).isEqualTo("IT work");
        assertThat(editTask.getProject()).isEqualTo("No project");
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New backlog item").contains("INBOX");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_related_project() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItemWithProject("New backlog item", "Business idea");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.string).isEqualTo("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isEqualTo("Business idea");

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList tasks = dayCaptain.day().tasks();
        assertThat(tasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = tasks.editLast();
        assertThat(editTask.getArea()).isEqualTo("Business");
        assertThat(editTask.getProject()).isEqualTo("Business idea");
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New backlog item").contains("INBOX");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_related_backlog_area() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createBacklogItem("New contact item", "IT work");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New contact item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isNull();

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList tasks = dayCaptain.day().tasks();
        assertThat(tasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = tasks.editLast();
        assertThat(editTask.getArea()).isEqualTo("IT work");
        assertThat(editTask.getProject()).isEqualTo("No project");
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New contact item").contains("IT work");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_related_backlog_project() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createBacklogItem("New contact item", "Business idea");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New contact item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isEqualTo("Business idea");

        backlogs.assignDayTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New day task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New day task");
        assertThat(editItem.getRelationNames().get(0)).contains(DATE_STRING_1).contains(DATE_STRING_2);
        editItem.close();

        TaskList tasks = dayCaptain.day().tasks();
        assertThat(tasks.getNames()).containsExactly("New day task");
        EditTaskAction editTask = tasks.editLast();
        assertThat(editTask.getArea()).isEqualTo("Business");
        assertThat(editTask.getProject()).isEqualTo("Business idea");
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New contact item").contains("Business idea");
        editTask.close();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTasks(DATE);
        system.deleteBacklogItemsInAllBacklogs("New backlog item", "New contact item");
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void tearDown() {
        dayCaptain.close();
    }

}
