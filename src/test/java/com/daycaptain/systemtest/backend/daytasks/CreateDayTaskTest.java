package com.daycaptain.systemtest.backend.daytasks;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testCreateDayTask() {
        LocalDate date = LocalDate.of(2020, 5, 9);

        dayCaptain.registerCountUpdates();
        URI taskId = dayCaptain.createDayTask("New task", date);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.note).isNull();
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.project).isNull();
        assertThat(task.area).isNull();
        assertThat(task.relatedProject).isNull();
        assertThat(task.relatedArea).isNull();
        assertThat(dayCaptain.getRegisteredUpdates()).isOne();

        assertThat(tasks.size()).isGreaterThan(2);
        assertThat(task.priority).isEqualTo(tasks.size());
    }

    @Test
    void testCreatePrioritizedDayTask() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI taskId = dayCaptain.createDayTask("New task", date, true);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(tasks.size()).isGreaterThan(2);
        assertThat(task.priority).isEqualTo(1);
    }

    @Test
    void testCreateDayTaskAssignedFromRelatedProject() {
        Task weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Working on my project");
        int assignedTaskSize = weekTask.assignedTasks.size();

        LocalDate date = LocalDate.of(2020, 5, 9);
        URI taskId = dayCaptain.createDayTask("New task, assigned from week task", date, weekTask);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task, assigned from week task");
        assertThat(task.assignedFromWeekTask).isEqualTo(weekTask._self);
        assertThat(task.project).isNull();
        assertThat(task.area).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");

        weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Working on my project");
        assertThat(weekTask.assignedTasks).hasSize(assignedTaskSize + 1);
    }

    @Test
    void testCreateDayTaskWithArea() {
        Task weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Something");
        int assignedTaskSize = weekTask.assignedTasks.size();

        LocalDate date = LocalDate.of(2020, 5, 9);
        URI taskId = dayCaptain.createDayTaskWithArea("New task, with area", date, 60, "IT work", weekTask, false);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with area");
        assertThat(task.assignedFromWeekTask).isEqualTo(weekTask._self);
        assertThat(task.project).isNull();
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedProject).isNull();
        assertThat(task.relatedArea).isEqualTo("IT work");

        weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Something");
        assertThat(weekTask.assignedTasks).hasSize(assignedTaskSize + 1);
    }

    @Test
    void testCreateDayTaskWithProject() {
        Task weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Something");
        int assignedTaskSize = weekTask.assignedTasks.size();

        LocalDate date = LocalDate.of(2020, 5, 9);
        URI taskId = dayCaptain.createDayTaskWithProject("New task, with project", date, 60, "Business idea", weekTask, false);
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with project");
        assertThat(task.assignedFromWeekTask).isEqualTo(weekTask._self);
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");

        weekTask = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Something");
        assertThat(weekTask.assignedTasks).hasSize(assignedTaskSize + 1);
    }

    @Test
    void testCreateDayTaskWithNote() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI taskId = dayCaptain.createDayTaskWithNote("New task, with note", date, "A note");
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with note");
        assertThat(task.note).isEqualTo("A note");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.project).isNull();
        assertThat(task.area).isNull();
        assertThat(task.relatedProject).isNull();
        assertThat(task.relatedArea).isNull();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.close();
    }

}
