package com.daycaptain.systemtest.backend.weektasks;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateWeekTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testCreateWeekTask() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTask("New task", week);
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.note).isNull();
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.project).isNull();
        assertThat(task.area).isNull();
        assertThat(task.relatedProject).isNull();
        assertThat(task.relatedArea).isNull();

        assertThat(tasks.size()).isGreaterThan(2);
        assertThat(task.priority).isEqualTo(tasks.size());
    }

    @Test
    void testCreateWeekTaskWithArea() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTaskWithArea("New task, with area", week, 60, "IT work");
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with area");
        assertThat(task.note).isNull();
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isNull();
        assertThat(task.area).isEqualTo("IT work");
        assertThat(task.relatedArea).isEqualTo("IT work");
    }

    @Test
    void testCreateWeekTaskWithProject() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTaskWithProject("New task, with project", week, 60, "Business idea");
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with project");
        assertThat(task.note).isNull();
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testCreateWeekTaskWithNote() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTaskWithNote("New task, with note", week, "A note");
        assertThat(taskId).isNotNull();

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, taskId);
        assertThat(task.string).isEqualTo("New task, with note");
        assertThat(task.note).isEqualTo("A note");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isNull();
        assertThat(task.relatedArea).isNull();
        assertThat(task.relatedProject).isNull();
    }

    @Test
    @Disabled("not implemented yet")
    void testCreateWeekTaskAssignedFromBacklogItemWithRelatedProject() {
        // assign from backlog item
    }

    // TODO missing, test relations

}
