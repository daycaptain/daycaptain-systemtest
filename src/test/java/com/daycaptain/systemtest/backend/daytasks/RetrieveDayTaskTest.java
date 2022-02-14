package com.daycaptain.systemtest.backend.daytasks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if tasks are equipped with all desired information for both list and get resources.
 */
public class RetrieveDayTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private final Set<LocalDate> dates = new HashSet<>();

    @Test
    void testGetDayTasks() {
        LocalDate date = LocalDate.of(2020, 5, 11);
        dates.add(date);
        URI taskId = dayCaptain.createDayTaskWithProject("New task", date, 60, "Business idea");
        assertThat(taskId).isNotNull();

        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(0);
        assertThat(task.current).isEqualTo(0);
        assertThat(task.status).isEqualTo("OPEN");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testGetDayTask() {
        LocalDate date = LocalDate.of(2020, 5, 11);
        dates.add(date);
        URI taskId = dayCaptain.createDayTaskWithProject("New task", date, 60, "Business idea");
        assertThat(taskId).isNotNull();

        Task task = dayCaptain.getTask(taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.string).isEqualTo("New task");
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(0);
        assertThat(task.current).isEqualTo(0);
        assertThat(task.status).isEqualTo("OPEN");
        assertThat(task.assignedFromWeekTask).isNull();
        assertThat(task.area).isNull();
        assertThat(task.project).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testGetDayTaskAssignedTime() {
        LocalDate date = LocalDate.of(2020, 6, 9);
        dates.add(date);
        URI taskId = dayCaptain.createDayTaskWithProject("New task", date, 60, "Business idea");
        Task task = dayCaptain.getTask(taskId);
        dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)), task);

        task = dayCaptain.getTask(taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(60);

        task = findTask(dayCaptain.getDay(date).tasks, taskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(60);
    }

    @Test
    @Disabled
    void testGetDayTaskCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        URI taskId = dayCaptain.createDayTaskWithProject("New task", now.toLocalDate(), 60, "Business idea");
        Task task = dayCaptain.getTask(taskId);
        dayCaptain.createDayTimeEvent("New event", now.minusMinutes(30), now.plusMinutes(30), task);

        task = dayCaptain.getTask(taskId);
        assertThat(task._self).isEqualTo(taskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(60);
        assertThat(task.current).isEqualTo(30);

        task = findTask(dayCaptain.getDay(now.toLocalDate()).tasks, taskId);
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.assigned).isEqualTo(60);
        assertThat(task.current).isEqualTo(30);
    }

    @Test
    void testGetDayTasksFilterArea() {
        LocalDate date = LocalDate.of(2020, 9, 29);
        dates.add(date);
        dayCaptain.createDayTaskWithArea("New task, area", date, "IT work");
        dayCaptain.createDayTask("New task", date);
        dayCaptain.createDayTaskWithArea("New task, another area", date, "Business");

        assertThat(dayCaptain.getDay(date).tasks).hasSize(3);
        assertThat(dayCaptain.getDayFilterArea(date, "IT work").tasks).extracting(e -> e.string).containsExactly("New task, area");
        assertThat(dayCaptain.getDayFilterArea(date, "Business").tasks).extracting(e -> e.string).containsExactly("New task, another area");
        assertThat(dayCaptain.getDayFilterArea(date, "Self-improvement").tasks).isEmpty();
        assertThat(dayCaptain.getDayFilterArea(date, "unknown").tasks).isEmpty();

        YearWeek week = YearWeek.from(date);
        assertThat(dayCaptain.getWeek(week).days.get(date).tasks).hasSize(3);
        assertThat(dayCaptain.getWeekFilterArea(week, "IT work").days.get(date).tasks).extracting(e -> e.string).containsExactly("New task, area");
        assertThat(dayCaptain.getWeekFilterArea(week, "Business").days.get(date).tasks).extracting(e -> e.string).containsExactly("New task, another area");
        assertThat(dayCaptain.getWeekFilterArea(week, "Self-improvement").days.getOrDefault(date, new Day()).tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterArea(week, "unknown").days.getOrDefault(date, new Day()).tasks).isEmpty();
    }

    @Test
    void testGetDayTasksFilterProject() {
        LocalDate date = LocalDate.of(2020, 9, 29);
        dates.add(date);
        dayCaptain.createDayTaskWithArea("New task, area", date, "IT work");
        dayCaptain.createDayTask("New task", date);
        dayCaptain.createDayTaskWithProject("New task, project", date, 60, "Business idea");

        assertThat(dayCaptain.getDay(date).tasks).hasSize(3);
        assertThat(dayCaptain.getDayFilterArea(date, "IT work").tasks).extracting(e -> e.string).containsExactly("New task, area");
        assertThat(dayCaptain.getDayFilterArea(date, "Business").tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getDayFilterArea(date, "Self-improvement").tasks).isEmpty();
        assertThat(dayCaptain.getDayFilterArea(date, "unknown").tasks).isEmpty();
        assertThat(dayCaptain.getDayFilterProject(date, "Business idea").tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getDayFilterProject(date, "Spanish").tasks).extracting(e -> e.string).isEmpty();
        assertThat(dayCaptain.getDayFilterProject(date, "unknown").tasks).isEmpty();

        YearWeek week = YearWeek.from(date);
        assertThat(dayCaptain.getWeek(week).days.get(date).tasks).hasSize(3);
        assertThat(dayCaptain.getWeekFilterArea(week, "IT work").days.get(date).tasks).extracting(e -> e.string).containsExactly("New task, area");
        assertThat(dayCaptain.getWeekFilterArea(week, "Business").days.get(date).tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getWeekFilterArea(week, "Self-improvement").days.getOrDefault(date, new Day()).tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterArea(week, "unknown").days.getOrDefault(date, new Day()).tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterProject(week, "Business idea").days.get(date).tasks).extracting(e -> e.string).containsExactly("New task, project");
        assertThat(dayCaptain.getWeekFilterProject(week, "Spanish").days.getOrDefault(date, new Day()).tasks).isEmpty();
        assertThat(dayCaptain.getWeekFilterProject(week, "unknown").days.getOrDefault(date, new Day()).tasks).isEmpty();
    }

    @AfterEach
    void tearDown() {
        dates.forEach(date -> {
            dayCaptain.deleteDayTasks(date);
            dayCaptain.deleteDayTimeEvents(date);
        });
    }

}
