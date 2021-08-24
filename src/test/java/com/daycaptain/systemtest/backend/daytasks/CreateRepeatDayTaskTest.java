package com.daycaptain.systemtest.backend.daytasks;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateRepeatDayTaskTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private final LocalDate date = LocalDate.of(2020, 5, 9);

    @Test
    void create_repeated_day_task_repeat_once() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createDayTaskRepeat(name, date, 1);

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(1)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(2)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void create_repeated_day_task_repeat_twice() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createDayTaskRepeat(name, date, 2);

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(1)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(2)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(3)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void create_repeated_day_task_repeat_7days_three_times() {
        String name = "New task " + UUID.randomUUID();
        dayCaptain.createDayTaskRepeat(name, date, 3, 7);

        List<Task> tasks = dayCaptain.getDay(date).tasks;
        Task task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(1)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();

        tasks = dayCaptain.getDay(date.plusDays(7)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(14)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(21)).tasks;
        task = CollectionUtils.findTask(tasks, name);
        assertThat(task.string).isEqualTo(name);

        tasks = dayCaptain.getDay(date.plusDays(28)).tasks;
        assertThat(CollectionUtils.findTask(tasks, name)).isNull();
    }

    @Test
    void invalid_repeated_day_task_negative_repeat_zero_repeat_cadence() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTaskRepeat("Invalid", date, -1));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTaskRepeat("Invalid", date, 1, 0));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTaskRepeat("Invalid", date, 1, -1));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
