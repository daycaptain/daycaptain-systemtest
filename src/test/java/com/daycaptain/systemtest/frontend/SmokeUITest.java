package com.daycaptain.systemtest.frontend;

import com.daycaptain.systemtest.frontend.views.DayView;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SmokeUITest {

    private final DayCaptainUI dayCaptain = new DayCaptainUI();

    @BeforeEach
    void setUp() {
        dayCaptain.initWithLogin();
    }

    @Test
    void dayView() {
        DayView dayView = dayCaptain.day();
        dayView.tasks().getNames();
        assertThat(dayCaptain.currentView()).endsWith("/day.html");
    }

    @Test
    void weekView() {
        WeekView weekView = dayCaptain.week();
        weekView.weekTasks().getList();
        assertThat(dayCaptain.currentView()).endsWith("/week.html");
    }

    @AfterEach
    void tearDown() {
        dayCaptain.close();
    }

}
