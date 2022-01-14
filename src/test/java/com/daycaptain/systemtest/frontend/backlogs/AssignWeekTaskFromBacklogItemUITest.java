package com.daycaptain.systemtest.frontend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.views.BacklogsView;
import com.daycaptain.systemtest.frontend.views.DayView;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignWeekTaskFromBacklogItemUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final YearWeek WEEK = YearWeek.now();
    private static final LocalDate DATE = LocalDate.now();
    private static final String WEEK_STRING = "Week " + WEEK.getWeek() + ", " + WEEK.getYear();

    @Test
    void create_task_from_backlog_item() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItem("New backlog item");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New backlog item");

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New backlog item").contains("INBOX");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_backlog() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createBacklogItem("New contact item", "To-contact");
        assertThat(backlogs.getCurrentBacklogItemNames()).last().isEqualTo("New contact item");

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New contact item").contains("To-contact");
        editTask.close();
    }

    @Test
    void assign_existing_task_from_backlog_item() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItem("New backlog item");
        backlogs.assertLastBacklogItemName("New backlog item");

        TaskList weekTasks = dayCaptain.week().weekTasks();
        CreateTaskAction action = weekTasks.create();
        action.setName("New week task");
        action.save();
        assertThat(weekTasks.getNames()).containsExactly("New week task");

        weekTasks.connectToItem("New backlog item", 0);
        assertThat(weekTasks.focused().hasRelation).isTrue();

        EditTaskAction editTask = weekTasks.editLast();
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New backlog item").contains("INBOX");
        editTask.close();
    }

    @Test
    void create_task_from_backlog_item_related_area() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItemWithArea("New backlog item", "i");
        backlogs.assertLastBacklogItemName("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.string).isEqualTo("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
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
        backlogs.assertLastBacklogItemName("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.string).isEqualTo("New backlog item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isEqualTo("Business idea");

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
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
        backlogs.assertLastBacklogItemName("New contact item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isNull();

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
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
        backlogs.assertLastBacklogItemName("New contact item");
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.hasArea).isEqualTo(true);
        assertThat(backlogs.getCurrentBacklogItems()).last().extracting(b -> b.project).isEqualTo("Business idea");

        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        EditBacklogItemAction editItem = backlogs.editBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1);
        assertThat(editItem.getRelationNames()).hasSize(1);
        assertThat(editItem.getRelationNames().get(0)).contains("New week task");
        assertThat(editItem.getRelationNames().get(0)).contains(WEEK_STRING);
        editItem.close();

        TaskList weekTasks = dayCaptain.week().weekTasks();
        assertThat(weekTasks.getNames()).containsExactly("New week task");
        EditTaskAction editTask = weekTasks.editLast();
        assertThat(editTask.getArea()).isEqualTo("Business");
        assertThat(editTask.getProject()).isEqualTo("Business idea");
        assertThat(editTask.getRelationNames()).hasSize(1);
        assertThat(editTask.getRelationNames().get(0)).contains("New contact item").contains("Business idea");
        editTask.close();
    }

    @Test
    void inherit_area_from_backlog_item_to_week_day_event() {
        BacklogsView backlogs = dayCaptain.backlogs();

        backlogs.createInboxItemWithArea("New backlog item", "i");
        backlogs.assignWeekTaskFromBacklogItem(backlogs.getCurrentBacklogItemNames().size() - 1, "New week task", 0);

        dayCaptain.week().assignDayTaskFromWeekTask(0, "New day task", 0);
        DayView day = dayCaptain.day();
        day.assignTimeEventFromDayTask(0, "New event", "12:00", "13:00");

        EditTimeEventAction editEvent = day.timeEvents().edit();
        assertThat(editEvent.getArea()).isEqualTo("IT work");
        assertThat(editEvent.getProject()).isEqualTo("No project");
        editEvent.close();

        WeekView week = dayCaptain.week();
        EditTaskAction editTask = week.dayTasksOffset(0).edit();
        assertThat(editTask.getArea()).isEqualTo("IT work");
        assertThat(editTask.getProject()).isEqualTo("No project");
        editTask.close();

        editEvent = week.dayTimeEventsOffset(0).edit();
        assertThat(editEvent.getArea()).isEqualTo("IT work");
        assertThat(editEvent.getProject()).isEqualTo("No project");
        editEvent.close();
    }

    @Test
    @Disabled
    void inherit_project_from_backlog_item_to_week_day_event() {
//        URI itemId = dayCaptain.createInboxItemWithProject("New backlog item", "Business idea");
//
//        BacklogItem item = dayCaptain.getBacklogItem(itemId);
//
//        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
//        Task weekTask = dayCaptain.getTask(taskId);
//
//        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
//        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
//        Task dayTask = dayCaptain.getTask(taskId);
//        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
//        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);
//
//        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
//        assertThat(event.area).isNull();
//        assertThat(event.relatedArea).isEqualTo("Business");
//        assertThat(event.project).isNull();
//        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

    @Test
    @Disabled
    void inherit_area_from_backlog_to_week_day_event() {
//        URI backlogId = dayCaptain.createBacklogWithArea("New backlog", "IT work");
//        Backlog backlog = dayCaptain.getBacklog(backlogId);
//        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);
//
//        BacklogItem item = dayCaptain.getBacklogItem(itemId);
//
//        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
//        Task weekTask = dayCaptain.getTask(taskId);
//
//        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
//        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
//        Task dayTask = dayCaptain.getTask(taskId);
//        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
//        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);
//
//        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
//        assertThat(event.area).isNull();
//        assertThat(event.relatedArea).isEqualTo("IT work");
//        assertThat(event.project).isNull();
//        assertThat(event.relatedProject).isNull();
    }

    @Test
    @Disabled
    void inherit_project_from_backlog_to_week_day_event() {
//        URI backlogId = dayCaptain.createBacklogWithProject("New backlog", "Business idea");
//        Backlog backlog = dayCaptain.getBacklog(backlogId);
//        URI itemId = dayCaptain.createBacklogItem("New backlog item", backlog);
//
//        BacklogItem item = dayCaptain.getBacklogItem(itemId);
//
//        URI taskId = dayCaptain.createWeekTask("New week task", WEEK, item);
//        Task weekTask = dayCaptain.getTask(taskId);
//
//        LocalDate date = WEEK.atDay(DayOfWeek.MONDAY);
//        taskId = dayCaptain.createDayTask("New day task", date, weekTask);
//        Task dayTask = dayCaptain.getTask(taskId);
//        URI eventId = dayCaptain.createDayTimeEvent("New time event", date.atTime(LocalTime.NOON), date.atTime(LocalTime.NOON.plusHours(1)), dayTask);
//        DayTimeEvent event = dayCaptain.getDayTimeEvent(eventId);
//
//        assertThat(event.assignedFromTask).isEqualTo(dayTask._self);
//        assertThat(event.area).isNull();
//        assertThat(event.relatedArea).isEqualTo("Business");
//        assertThat(event.project).isNull();
//        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

    @BeforeEach
    void beforeEach() {
        system.deleteWeekTasks(WEEK);
        system.deleteDayTasks(DATE);
        system.deleteDayTimeEvents(DATE);
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
