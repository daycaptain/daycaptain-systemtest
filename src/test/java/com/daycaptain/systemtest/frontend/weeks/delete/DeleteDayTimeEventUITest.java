package com.daycaptain.systemtest.frontend.weeks.delete;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDayTimeEventUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();

    private DayTimeEventList dayTimeEvents;

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
        system.deleteDayTimeEvents(date);
        WeekView weekView = dayCaptain.week(week);
        dayTimeEvents = weekView.dayTimeEvents(dayOfWeek);
    }

    @Test
    void deleteWithEqualStartTime() {
        dayTimeEvents.createSave("Task 1");
        dayTimeEvents.createSave("Task 2");
        dayTimeEvents.delete(1);
        // is this desired behaviour?
        assertThat(dayTimeEvents.getNames()).containsExactly("Task 2");
    }

}
