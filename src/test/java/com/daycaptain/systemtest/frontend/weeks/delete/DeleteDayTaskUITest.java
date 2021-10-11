package com.daycaptain.systemtest.frontend.weeks.delete;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDayTaskUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();

    private TaskList dayTasks;

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

    @BeforeEach
    void setUp() {
        YearWeek week = YearWeek.of(2021, 7);
        LocalDate date = LocalDate.of(2021, 2, 16);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        system.deleteDayTasks(date);
        WeekView weekView = dayCaptain.week(week);
        dayTasks = weekView.dayTasks(dayOfWeek);
    }

    @Test
    void delete() {
        dayTasks.createSave("Task 1");
        dayTasks.createSave("Task 2");
        dayTasks.delete(1);
        assertThat(dayTasks.getNames()).containsExactly("Task 1");
    }

}
