package com.daycaptain.systemtest.backend.weektasks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if tasks are equipped with all desired information for both list and get resources.
 */
public class RetrieveWeekTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testGetWeekTasks() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTaskWithProject("New task", week, 60, "Business idea");
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getWeek(week).tasks, taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.status).isEqualTo("OPEN");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testGetWeekTask() {
        YearWeek week = YearWeek.of(2020, 19);
        URI taskId = dayCaptain.createWeekTaskWithProject("New task", week, 60, "Business idea");
        assertThat(taskId).isNotNull();

        Task task = dayCaptain.getTask(taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.status).isEqualTo("OPEN");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testGetWeekTaskAssignedTime() {
        YearWeek week = YearWeek.of(2020, 21);
        URI weekTaskId = dayCaptain.createWeekTaskWithProject("New task", week, 60, "Business idea");
        assertThat(weekTaskId).isNotNull();
        Task task = dayCaptain.getTask(weekTaskId);

        LocalDate date = week.atDay(DayOfWeek.MONDAY);
        dayCaptain.createDayTimeEvent("New event, from week task", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)), task);

        date = week.atDay(DayOfWeek.TUESDAY);
        URI taskId = dayCaptain.createDayTask("New task, from week task", date, task);

        task = dayCaptain.getTask(taskId);
        dayCaptain.createDayTimeEvent("New event, from day task", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)), task);
        dayCaptain.createDayTimeEvent("New event, from day task", LocalDateTime.of(date, LocalTime.of(14, 0)), LocalDateTime.of(date, LocalTime.of(14, 30)), task);

        task = dayCaptain.getTask(weekTaskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(150);

        task = findTask(dayCaptain.getWeek(week).tasks, weekTaskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(150);
    }

    @Test
    void testGetWeekTasksFilterArea() {
        YearWeek week = YearWeek.of(2020, 22);
        dayCaptain.createWeekTaskWithArea("New task, area", week, 60, "IT work");
        dayCaptain.createWeekTask("New task", week);
        dayCaptain.createWeekTaskWithArea("New task, another area", week, 60, "Business");

        assertThat(dayCaptain.getWeek(week).tasks).hasSize(3);
        assertThat(dayCaptain.getWeekFilterArea(week, "IT work").tasks).extracting(e -> e.string).containsExactly("New task, area");
        assertThat(dayCaptain.getWeekFilterArea(week, "Business").tasks).extracting(e -> e.string).containsExactly("New task, another area");
        assertThat(dayCaptain.getWeekFilterArea(week, "Self-improvement").tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterArea(week, "unknown").tasks).isEmpty();
    }

    @Test
    void testGetWeekTasksFilterProject() {
        YearWeek week = YearWeek.of(2020, 22);
        dayCaptain.createWeekTaskWithProject("New task, project", week, 60, "Business idea");
        dayCaptain.createWeekTask("New task", week);
        dayCaptain.createWeekTaskWithProject("New task, another project", week, 60, "Spanish");

        assertThat(dayCaptain.getWeek(week).tasks).hasSize(3);
        assertThat(dayCaptain.getWeekFilterArea(week, "Business").tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getWeekFilterArea(week, "Self-improvement").tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterArea(week, "unknown").tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterProject(week, "Business idea").tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getWeekFilterProject(week, "Spanish").tasks).extracting(e -> e.string).containsExactly("New task, another project");
        assertThat(dayCaptain.getWeekFilterProject(week, "unknown").tasks).isEmpty();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteWeekTasks(YearWeek.of(2020, 22));
    }

}
