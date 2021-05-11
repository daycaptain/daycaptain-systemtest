package com.daycaptain.systemtest.frontend.weeks.delete;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteWeekTaskUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();

    private TaskList weekTasks;

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
        system.deleteWeekTasks(week);
        weekTasks = dayCaptain.week(week).weekTasks();
    }

    @Test
    void delete() {
        weekTasks.create("Task 1");
        weekTasks.create("Task 2");
        weekTasks.delete(1);
        assertThat(weekTasks.getNames()).containsExactly("Task 1");
    }

}
