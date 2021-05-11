package com.daycaptain.systemtest.backend.actions;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class UndoWeekTasksTest {

    private static final YearWeek WEEK = YearWeek.of(2020, 49);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUndoTaskDeletion() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(YearWeek.parse(task.dueTime)).isEqualTo(week);

        String actionId = dayCaptain.deleteTask(task);
        tasks = dayCaptain.getWeek(week).tasks;
        Assertions.assertThat(CollectionUtils.findTask(tasks, taskId)).isNull();

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getWeek(week).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(YearWeek.parse(task.dueTime)).isEqualTo(week);
    }

    @Test
    void testUndoTaskUpdate() {
        YearWeek week = WEEK;
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(YearWeek.parse(task.dueTime)).isEqualTo(week);

        String actionId = dayCaptain.updateTask(task, "string", "New task, task");

        tasks = dayCaptain.getWeek(week).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, task");

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getWeek(week).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
    }

    @Test
    void testUndoTaskResort() {
        YearWeek week = WEEK;

        URI taskId = dayCaptain.createWeekTask("First", week);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createWeekTask("Second", week);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createWeekTask("Third", week);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        String actionId = dayCaptain.updateTask(tasks.get(1), "priority", 1);
        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getWeek(week).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteWeekTasks(WEEK);
    }

}
