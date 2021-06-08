package com.daycaptain.systemtest.backend.daytasks;


import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateDayTaskTest {

    private static final LocalDate DATE = LocalDate.of(2020, 5, 9);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUpdateString() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");

        dayCaptain.updateTask(task, "string", "Very new task");

        task = dayCaptain.getTask(task._self);
        assertThat(task.string).isEqualTo("Very new task");
    }

    @Test
    void testUpdatePlanned() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.planned).isEqualTo(0);

        dayCaptain.updateTask(task, "planned", 60);

        task = dayCaptain.getTask(task._self);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
    }

    @Test
    void testResort() {
        LocalDate date = LocalDate.of(2020, 4, 8);

        dayCaptain.deleteDayTasks(date);

        URI taskId = dayCaptain.createDayTask("First", date);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createDayTask("Second", date);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createDayTask("Third", date);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to top
        dayCaptain.updateTask(tasks.get(1), "priority", 1);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to end
        dayCaptain.updateTask(tasks.get(1), "priority", 3);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to top
        dayCaptain.updateTask(tasks.get(2), "priority", 1);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to middle
        dayCaptain.updateTask(tasks.get(2), "priority", 2);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Third", "Second");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to end
        dayCaptain.updateTask(tasks.get(0), "priority", 3);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Third", "Second", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to middle
        dayCaptain.updateTask(tasks.get(0), "priority", 2);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @Test
    void testAssignFromWeekTask() {
        // test is correct, backend not implemented yet

        LocalDate date = DATE;
        URI dayTaskId = dayCaptain.createDayTask("New task", date);
        assertThat(dayTaskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, dayTaskId);
        assertThat(task.string).isEqualTo("New task");

        URI weekTaskId = dayCaptain.createWeekTask("Week task", YearWeek.from(date));
        assertThat(weekTaskId).isNotNull();

        dayCaptain.addRelation(task, weekTaskId);

        task = dayCaptain.getTask(task._self);
        assertThat(task._self).isEqualTo(dayTaskId);
        assertThat(task.assignedFromWeekTask).isEqualTo(weekTaskId);
    }

    @Test
    void testAddProject() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();

        dayCaptain.updateTask(task, "project", "Business idea");

        task = dayCaptain.getTask(task._self);
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testUpdateProject() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.project).isNull();

        dayCaptain.updateTask(task, "project", "Business idea");
        task = dayCaptain.getTask(task._self);
        assertThat(task.project).isEqualTo("Business idea");

        dayCaptain.updateTask(task, "project", "Work presentations");
        task = dayCaptain.getTask(task._self);
        assertThat(task.project).isEqualTo("Work presentations");
        assertThat(task.relatedProject).isEqualTo("Work presentations");
    }

    @Test
    void testRemoveProject() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();

        dayCaptain.updateTask(task, "project", "Business idea");
        task = dayCaptain.getTask(task._self);
        assertThat(task.project).isEqualTo("Business idea");

        dayCaptain.updateTask(task, "project", null);
        task = dayCaptain.getTask(task._self);
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    void testAddInvalidProject() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateTask(task, "project", "invalid"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testAddArea() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isNull();

        dayCaptain.updateTask(task, "area", "IT work");

        task = dayCaptain.getTask(task._self);
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedArea).isEqualTo("IT work");
    }

    @Test
    void testUpdateArea() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();
        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);

        dayCaptain.updateTask(task, "area", "IT work");
        task = dayCaptain.getTask(task._self);
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedArea).isEqualTo("IT work");

        dayCaptain.updateTask(task, "area", "Self-improvement");
        task = dayCaptain.getTask(task._self);
        assertThat(task.area).isEqualTo("Self-improvement");
        assertThat(task.relatedArea).isEqualTo("Self-improvement");
    }

    @Test
    void testRemoveArea() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();
        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);

        dayCaptain.updateTask(task, "area", "IT work");
        task = dayCaptain.getTask(task._self);
        assertThat(task.area).isEqualTo("IT work");

        dayCaptain.updateTask(task, "area", null);
        task = dayCaptain.getTask(task._self);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isNull();
    }

    @Test
    void testAddInvalidArea() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateTask(task, "area", "invalid"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testSetProjectRemovesArea() {
        URI taskId = dayCaptain.createDayTaskWithArea("New task", DATE, "IT work");

        Task task = findTask(dayCaptain.getDay(DATE).tasks, taskId);
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedArea).isEqualTo("IT work");

        dayCaptain.updateTask(task, "project", "Business idea");
        task = findTask(dayCaptain.getDay(DATE).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testSetAreaRemovesProject() {
        URI taskId = dayCaptain.createDayTaskWithProject("New task", DATE, 0, "Business idea");

        Task task = findTask(dayCaptain.getDay(DATE).tasks, taskId);
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedProject).isEqualTo("Business idea");

        dayCaptain.updateTask(task, "area", "IT work");
        task = findTask(dayCaptain.getDay(DATE).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedArea).isEqualTo("IT work");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    void testCannotSetBothAreaAndProject() {
        URI taskId = dayCaptain.createDayTask("New task", DATE);
        Task task = findTask(dayCaptain.getDay(DATE).tasks, taskId);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateTask(task, "area", "IT work", "project", "Business idea"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
