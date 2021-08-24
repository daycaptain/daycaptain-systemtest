package com.daycaptain.systemtest.frontend.weeks.create;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateRepeatedWeekTasksUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);

    @Test
    void create_repeated_week_task_repeat_once() {
        String name = "New task " + UUID.randomUUID();
        WeekView view = dayCaptain.week(week);
        CreateTaskAction action = view.weekTasks().create();
        action.setName(name);
        action.setRepeated(1, 1);
        action.save();

        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).doesNotContain(name);
    }

    @Test
    void create_repeated_week_task_repeat_twice() {
        String name = "New task " + UUID.randomUUID();
        WeekView view = dayCaptain.week(week);
        CreateTaskAction action = view.weekTasks().create();
        action.setName(name);
        action.setRepeated(2, 1);
        action.save();

        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).doesNotContain(name);
    }

    @Test
    void create_repeated_week_task_repeat_7weeks_three_times() {
        String name = "New task " + UUID.randomUUID();
        WeekView view = dayCaptain.week(week);
        CreateTaskAction action = view.weekTasks().create();
        action.setName(name);
        action.setRepeated(3, 7);
        action.save();

        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeek();
        assertThat(view.weekTasks().getNames()).doesNotContain(name);

        view.nextWeeks(6);
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeeks(7);
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeeks(7);
        assertThat(view.weekTasks().getNames()).contains(name);

        view.nextWeeks(7);
        assertThat(view.weekTasks().getNames()).doesNotContain(name);
    }

    @AfterEach
    void tearDown() {
        system.deleteWeekTasks(week);
        system.deleteWeekTasks(week.plusWeeks(1));
        system.deleteWeekTasks(week.plusWeeks(2));
        system.deleteWeekTasks(week.plusWeeks(7));
        system.deleteWeekTasks(week.plusWeeks(14));
        system.deleteWeekTasks(week.plusWeeks(21));
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
