package com.daycaptain.systemtest.backend.weektasks;


import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateWeekTaskTest {

    private static final YearWeek WEEK = YearWeek.of(2020, 19);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUpdateString() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");

        dayCaptain.updateTask(task, "string", "Very new task");

        task = dayCaptain.getTask(task._self);
        assertThat(task.string).isEqualTo("Very new task");
    }

    @Test
    void testUpdatePlanned() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
        assertThat(task.planned).isEqualTo(0);

        dayCaptain.updateTask(task, "planned", 60);

        task = dayCaptain.getTask(task._self);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
    }

    @Test
    void testResort() {
        YearWeek week = YearWeek.of(2020, 18);

        dayCaptain.deleteWeekTasks(week);

        URI taskId = dayCaptain.createWeekTask("First", week);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createWeekTask("Second", week);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createWeekTask("Third", week);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to top
        dayCaptain.updateTask(tasks.get(1), "priority", 1);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to end
        dayCaptain.updateTask(tasks.get(1), "priority", 3);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to top
        dayCaptain.updateTask(tasks.get(2), "priority", 1);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to middle
        dayCaptain.updateTask(tasks.get(2), "priority", 2);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Third", "Second");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to end
        dayCaptain.updateTask(tasks.get(0), "priority", 3);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Third", "Second", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to middle
        dayCaptain.updateTask(tasks.get(0), "priority", 2);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @Test
    void testAddProject() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateTask(task, "project", "invalid"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testAddArea() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();
        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);

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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();
        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);

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
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.area).isNull();
        assertThat(task.relatedArea).isNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateTask(task, "area", "invalid"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
