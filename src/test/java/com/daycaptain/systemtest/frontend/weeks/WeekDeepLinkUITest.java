package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekDeepLinkUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);

    @Test
    void week_task_deep_link_selected() {
        system.createWeekTask("Week task 1", week);
        URI uri = system.createWeekTask("Week task 2", week);
        system.createWeekTask("Week task 3", week);
        assertThat(dayCaptain.week(week.minusWeeks(1)).getDateHeader()).contains("Week 6, 2021");

        WeekView weekView = dayCaptain.weekLink(uri);
        EditTaskAction edit = weekView.focusedWeekTasks().edit();
        assertThat(edit.getName()).isEqualTo("Week task 2");
    }

    @BeforeEach
    void beforeEach() {
        system.deleteWeekTasks(week);
    }

    @BeforeAll
    public static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    public static void afterAll() {
        dayCaptain.close();
    }

}
