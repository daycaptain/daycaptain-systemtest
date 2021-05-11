package com.daycaptain.systemtest.backend.actions;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class UndoDayTasksTest {

    private static final LocalDate DATE = LocalDate.of(2020, 12, 2);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUndoTaskDeletion() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(LocalDate.parse(task.dueTime)).isEqualTo(date);

        String actionId = dayCaptain.deleteTask(task);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(CollectionUtils.findTask(tasks, taskId)).isNull();

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getDay(date).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(LocalDate.parse(task.dueTime)).isEqualTo(date);
    }

    @Test
    void testUndoTaskUpdate() {
        LocalDate date = DATE;
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(LocalDate.parse(task.dueTime)).isEqualTo(date);

        String actionId = dayCaptain.updateTask(task, "string", "New task, task");

        tasks = dayCaptain.getDay(date).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, task");

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getDay(date).tasks;
        task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
    }

    @Test
    void testUndoTaskResort() {
        LocalDate date = DATE;

        URI taskId = dayCaptain.createDayTask("First", date);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createDayTask("Second", date);
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createDayTask("Third", date);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        String actionId = dayCaptain.updateTask(tasks.get(1), "priority", 1);
        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);

        dayCaptain.undo(actionId);

        tasks = dayCaptain.getDay(date).tasks;
        assertThat(tasks).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(tasks).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTasks(DATE);
    }

}
