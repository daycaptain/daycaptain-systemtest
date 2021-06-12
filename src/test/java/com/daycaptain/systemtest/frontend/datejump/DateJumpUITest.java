package com.daycaptain.systemtest.frontend.datejump;

import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.DateJump;
import com.daycaptain.systemtest.frontend.views.DayView;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class DateJumpUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();

    @Test
    void day_date_jump_to_date() {
        DateJump dateJump = dayCaptain.day().dateJump();
        dateJump.gotoDate(28, 2, 21);
        assertThat(new DayView().getDateHeader()).isEqualTo("Sun, 28th February 2021");
    }

    @Test
    void day_date_jump_to_today() {
        DayView day = dayCaptain.day();
        String dateHeader = day.getDateHeader();
        DateJump dateJump = day.dateJump();
        LocalDate today = LocalDate.now();
        dateJump.gotoDate(today.getDayOfMonth(), today.getMonthValue(), today.getYear() % 100);
        assertThat(new DayView().getDateHeader()).isEqualTo(dateHeader);
    }

    @Test
    void week_date_jump_to_date() {
        DateJump dateJump = dayCaptain.week().dateJump();
        dateJump.gotoDate(28, 2, 21);
        WeekView week = new WeekView();
        assertThat(week.selectedDay()).isEqualTo("Sun, 28th Feb");
        assertThat(week.getDateHeader()).isEqualTo("Week 8, 2021 (from 22nd Feb)");
    }

    @Test
    void week_date_jump_to_date_same_week() {
        DateJump dateJump = dayCaptain.week(YearWeek.of(2021, 8)).dateJump();
        dateJump.gotoDate(28, 2, 21);
        WeekView week = new WeekView();
        assertThat(week.selectedDay()).isEqualTo("Sun, 28th Feb");
        assertThat(week.getDateHeader()).isEqualTo("Week 8, 2021 (from 22nd Feb)");
    }

    @Test
    void week_date_jump_to_date_today() {
        WeekView week = dayCaptain.week();
        String dateHeader = week.getDateHeader();
        DateJump dateJump = week.dateJump();
        LocalDate today = LocalDate.now();
        dateJump.gotoDate(today.getDayOfMonth(), today.getMonthValue(), today.getYear() % 100);
        assertThat(week.selectedDay()).contains(today.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US));
        assertThat(week.getDateHeader()).isEqualTo(dateHeader);
    }

    @Test
    void backlogs_date_jump_to_date() {
        DateJump dateJump = dayCaptain.backlogs().dateJump();
        dateJump.gotoDate(28, 2, 21);
        assertThat(new DayView().getDateHeader()).isEqualTo("Sun, 28th February 2021");
        assertThat(dayCaptain.currentView()).contains("day.html");
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
