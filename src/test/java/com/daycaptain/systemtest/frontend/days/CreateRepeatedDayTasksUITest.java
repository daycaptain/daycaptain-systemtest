package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateRepeatedDayTasksUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);

    @Test
    void create_repeated_day_task_repeat_once() {
        String name = "New task " + UUID.randomUUID();
        DayView view = dayCaptain.day(date);
        CreateTaskAction action = view.tasks().create();
        action.setName(name);
        action.setRepeated(1, 1);
        action.save();

        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).doesNotContain(name);
    }

    @Test
    void create_repeated_day_task_repeat_twice() {
        String name = "New task " + UUID.randomUUID();
        DayView view = dayCaptain.day(date);
        CreateTaskAction action = view.tasks().create();
        action.setName(name);
        action.setRepeated(2, 1);
        action.save();

        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).doesNotContain(name);
    }

    @Test
    void create_repeated_day_task_repeat_7weeks_three_times() {
        String name = "New task " + UUID.randomUUID();
        DayView view = dayCaptain.day(date);
        CreateTaskAction action = view.tasks().create();
        action.setName(name);
        action.setRepeated(3, 7);
        action.save();

        assertThat(view.tasks().getNames()).contains(name);

        view.nextDay();
        assertThat(view.tasks().getNames()).doesNotContain(name);

        view.nextDays(6);
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDays(7);
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDays(7);
        assertThat(view.tasks().getNames()).contains(name);

        view.nextDays(7);
        assertThat(view.tasks().getNames()).doesNotContain(name);
    }

    @AfterEach
    void tearDown() {
        system.deleteDayTasks(date);
        system.deleteDayTasks(date.plusDays(1));
        system.deleteDayTasks(date.plusDays(2));
        system.deleteDayTasks(date.plusDays(7));
        system.deleteDayTasks(date.plusDays(14));
        system.deleteDayTasks(date.plusDays(21));
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

}
