package com.daycaptain.systemtest.backend;

import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SmokeTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testSmokeTest() {
        Day dayResources = dayCaptain.getDay(LocalDate.of(2020, 5, 8));
        assertThat(dayResources.tasks).isNotEmpty();
    }

    @Test
    void testMasterData() {
        Day dayResources = dayCaptain.getDay(LocalDate.of(2020, 5, 8));

        Task task = CollectionUtils.findTask(dayResources.tasks, "Working on my project");
        assertThat(task.status).isEqualTo("OPEN");
        assertThat(task.dueTime).isEqualTo("2020-05-08");
        assertThat(task.planned).isEqualTo(60);
        assertThat(task.priority).isEqualTo(1);
        assertThat(task.area).isNull();
        assertThat(task.project).isNull();
        assertThat(task.relatedProject).isEqualTo("Business idea");
        assertThat(task.relatedArea).isEqualTo("Business");
        assertThat(task.assignedFromWeekTask.toString()).endsWith("/2020-W19/tasks/80db702b-7e58-4801-bb5b-d33cf1673859");
        assertThat(task.assignedTasks).isEmpty();
        assertThat(task.assignedDayTimeEvents).hasSize(1);
        assertThat(task.assignedDayTimeEvents.iterator().next().toString()).endsWith("2020-05-08/day-time-events/bc58f2cf-25a6-46e4-9c5c-97bdc3e30ed4");

        DayTimeEvent timeEvent = CollectionUtils.findDayTimeEvent(dayResources.timeEvents, "Working on my project");
        assertThat(timeEvent.start).isEqualTo("2020-05-08T08:00:00+02:00[Europe/Berlin]");
        assertThat(timeEvent.end).isEqualTo("2020-05-08T11:00:00+02:00[Europe/Berlin]");
        assertThat(timeEvent.area).isNull();
        assertThat(timeEvent.project).isNull();
        assertThat(timeEvent.relatedProject).isEqualTo("Business idea");
        assertThat(timeEvent.relatedArea).isEqualTo("Business");
        assertThat(timeEvent.assignedFromTask).isEqualTo(task._self);
    }

}