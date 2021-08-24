package com.daycaptain.systemtest.backend.weektasks;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateRepeatWeekTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private final YearWeek week = YearWeek.of(2020, 19);

    @Test
    void create_repeated_week_task_repeat_once() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createWeekTaskRepeat(name, week, 1);

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(1)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(2)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void create_repeated_week_task_repeat_twice() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createWeekTaskRepeat(name, week, 2);

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(1)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(2)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(3)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void create_repeated_week_task_repeat_7weeks_three_times() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createWeekTaskRepeat(name, week, 3, 7);

        List<Task> tasks = dayCaptain.getWeek(week).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(1)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();

        tasks = dayCaptain.getWeek(week.plusWeeks(7)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(14)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(21)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getWeek(week.plusWeeks(28)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void invalid_repeated_week_task_negative_repeat_zero_repeat_cadence() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createWeekTaskRepeat("Invalid", week, -1));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createWeekTaskRepeat("Invalid", week, 1, 0));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createWeekTaskRepeat("Invalid", week, 1, -1));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
