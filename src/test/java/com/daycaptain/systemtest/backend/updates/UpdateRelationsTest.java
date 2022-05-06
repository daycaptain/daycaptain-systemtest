package com.daycaptain.systemtest.backend.updates;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static com.daycaptain.systemtest.backend.CollectionUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateRelationsTest {

    private static final LocalDate DATE = LocalDate.of(2020, 4, 1);
    private static final YearWeek WEEK = YearWeek.from(DATE);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testAddRemoveWeekTaskRelations() {
        Week week = dayCaptain.getWeek(WEEK);
        Task weekTask = findTask(week.tasks, "Task");
        Task dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).isEmpty();
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isNull();
        assertThat(dayTask.assignedFromBacklogTask).isNull();

        dayCaptain.addRelation(weekTask._self, dayTask._self);

        week = dayCaptain.getWeek(WEEK);
        weekTask = findTask(week.tasks, "Task");
        dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).containsExactly(dayTask._self);
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isEqualTo(weekTask._self);
        assertThat(dayTask.assignedFromBacklogTask).isNull();

        dayCaptain.removeRelation(weekTask._self, dayTask._self);

        week = dayCaptain.getWeek(WEEK);
        weekTask = findTask(week.tasks, "Task");
        dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).isEmpty();
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isNull();
        assertThat(dayTask.assignedFromBacklogTask).isNull();
    }

    @Test
    void testAddRemoveDayTaskRelations() {
        Week week = dayCaptain.getWeek(WEEK);
        Task weekTask = findTask(week.tasks, "Task");
        Task dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).isEmpty();
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isNull();
        assertThat(dayTask.assignedFromBacklogTask).isNull();

        dayCaptain.addRelation(dayTask._self, weekTask._self);

        week = dayCaptain.getWeek(WEEK);
        weekTask = findTask(week.tasks, "Task");
        dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).containsExactly(dayTask._self);
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isEqualTo(weekTask._self);
        assertThat(dayTask.assignedFromBacklogTask).isNull();

        dayCaptain.removeRelation(dayTask._self, weekTask._self);

        week = dayCaptain.getWeek(WEEK);
        weekTask = findTask(week.tasks, "Task");
        dayTask = findTask(week.days.get(DATE).tasks, "Task");

        assertThat(weekTask.assignedTasks).isEmpty();
        assertThat(weekTask.assignedFromBacklogTask).isNull();
        assertThat(dayTask.assignedFromWeekTask).isNull();
        assertThat(dayTask.assignedFromBacklogTask).isNull();
    }

    @Test
    void testAddRemoveDayTimeEventRelations() {
        Day day = dayCaptain.getDay(DATE);
        DayTimeEvent event = findDayTimeEvent(day.timeEvents, "Event");
        Task task = findTask(day.tasks, "Task");

        assertThat(event.assignedFromTask).isNull();
        assertThat(task.assignedDayTimeEvents).isEmpty();

        dayCaptain.addRelation(event._self, task._self);

        day = dayCaptain.getDay(DATE);
        event = findDayTimeEvent(day.timeEvents, "Event");
        task = findTask(day.tasks, "Task");

        assertThat(event.assignedFromTask).isEqualTo(task._self);
        assertThat(task.assignedDayTimeEvents).contains(event._self);

        dayCaptain.removeRelation(event._self, task._self);

        day = dayCaptain.getDay(DATE);
        event = findDayTimeEvent(day.timeEvents, "Event");
        task = findTask(day.tasks, "Task");

        assertThat(event.assignedFromTask).isNull();
        assertThat(task.assignedDayTimeEvents).isEmpty();
    }

    @Test
    void testAddRemoveBacklogItemRelations() {
        BacklogItem backlogItem = findBacklogItem(dayCaptain.getInbox().items, "UpdateRelationsTest task");
        Task task = findTask(dayCaptain.getDay(DATE).tasks, "Task");

        assertThat(backlogItem.assignedTasks).isEmpty();
        assertThat(task.assignedFromBacklogTask).isNull();

        dayCaptain.addRelation(backlogItem._self, task._self);

        backlogItem = findBacklogItem(dayCaptain.getInbox().items, "UpdateRelationsTest task");
        task = findTask(dayCaptain.getDay(DATE).tasks, "Task");

        assertThat(backlogItem.assignedTasks).contains(task._self);
        assertThat(task.assignedFromBacklogTask).isEqualTo(backlogItem._self);

        dayCaptain.removeRelation(backlogItem._self, task._self);

        backlogItem = findBacklogItem(dayCaptain.getInbox().items, "UpdateRelationsTest task");
        task = findTask(dayCaptain.getDay(DATE).tasks, "Task");

        assertThat(backlogItem.assignedTasks).isEmpty();
        assertThat(task.assignedFromBacklogTask).isNull();
    }

    @BeforeEach
    void setUp() {
        dayCaptain.createDayTask("Task", DATE);
        dayCaptain.createWeekTask("Task", WEEK);
        dayCaptain.createDayTimeEvent("Event", time(DATE, 10), time(DATE, 11));
        Backlog backlog = findBacklog(dayCaptain.getBacklogs(), "To-contact");
        dayCaptain.createBacklogItem("UpdateRelationsTest task", backlog);
        dayCaptain.createInboxItem("UpdateRelationsTest task");
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteWeekTasks(WEEK);
        dayCaptain.deleteDayTimeEvents(DATE);
        dayCaptain.deleteBacklogItemsInAllBacklogs("UpdateRelationsTest task");
    }

}
