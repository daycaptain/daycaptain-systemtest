package com.daycaptain.systemtest.frontend;

import com.daycaptain.systemtest.frontend.views.DayView;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        assertThat(dayCaptain.currentView()).endsWith("/day.html#" + LocalDate.now());
    }

    @Test
    void weekView() {
        WeekView weekView = dayCaptain.week();
        weekView.weekTasks().getList();
        assertThat(dayCaptain.currentView()).endsWith("/week.html#"+ YearWeek.now());
    }

    @AfterEach
    void tearDown() {
        dayCaptain.close();
    }

}
