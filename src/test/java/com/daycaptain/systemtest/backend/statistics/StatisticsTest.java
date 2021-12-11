package com.daycaptain.systemtest.backend.statistics;

import com.daycaptain.systemtest.Times;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class StatisticsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    private static final LocalDate DATE = LocalDate.of(2021, 4, 16);

    @BeforeEach
    void setUp() {
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteDayTimeEvents(DATE);
    }

    @Test
    void planned_actual_statistics_sorted_times() {
        URI uri = dayCaptain.createDayTaskWithProject("Business task", DATE, 60, "Business idea");
        Task task = dayCaptain.getTask(uri);
        dayCaptain.createDayTimeEvent("Business task", timeHour(10), timeHour(12), task);

        uri = dayCaptain.createDayTaskWithProject("Spanish task", DATE, 0, "Spanish");
        task = dayCaptain.getTask(uri);
        dayCaptain.createDayTimeEvent("Spanish task", timeHour(14), timeHour(15), task);

        dayCaptain.createDayTaskWithProject("Work task", DATE, 90, "Work presentations");

        Day day = dayCaptain.getDay(LocalDate.of(2021, 4, 16));
        assertThat(day.statistics.planned.projects.keys).containsExactly("Work presentations", "Business idea");
        assertThat(day.statistics.actual.projects.keys).containsExactly("Business idea", "Spanish");

        System.out.println(day.statistics.actual.projects.keys);
        System.out.println(day.statistics.planned.areas.keys);
        System.out.println(day.statistics.actual.areas.keys);
    }

    @Test
    void planned_actual_statistics_actual_follows_planned_sorting() {
        URI uri = dayCaptain.createDayTaskWithProject("Business task", DATE, 120, "Business idea");
        Task task = dayCaptain.getTask(uri);
        dayCaptain.createDayTimeEvent("Business task", timeHour(10), timeHour(12), task);

        uri = dayCaptain.createDayTaskWithProject("Spanish task", DATE, 0, "Spanish");
        task = dayCaptain.getTask(uri);
        dayCaptain.createDayTimeEvent("Spanish task", timeHour(14), time(16, 30), task);

        uri = dayCaptain.createDayTaskWithProject("Work task", DATE, 90, "Work presentations");
        task = dayCaptain.getTask(uri);
        dayCaptain.createDayTimeEvent("Work task", timeHour(18), time(21, 30), task);

        Day day = dayCaptain.getDay(LocalDate.of(2021, 4, 16));
        assertThat(day.statistics.planned.projects.keys).containsExactly("Business idea", "Work presentations");
        assertThat(day.statistics.actual.projects.keys).containsExactly("Business idea", "Work presentations", "Spanish");

        assertThat(day.statistics.planned.areas.keys).containsExactly("Business", "IT work");
        assertThat(day.statistics.actual.areas.keys).containsExactly("Business", "IT work");
    }

    private LocalDateTime timeHour(int hour) {
        return Times.time(DATE, hour);
    }

    private LocalDateTime time(int hour, int minute) {
        return Times.time(DATE, hour, minute);
    }

}
