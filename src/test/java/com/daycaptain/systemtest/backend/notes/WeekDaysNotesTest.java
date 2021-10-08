package com.daycaptain.systemtest.backend.notes;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekDaysNotesTest {

    private static final LocalDate date = LocalDate.of(2021, 5, 9);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void week_resource_doesnt_return_daily_note() {
        dayCaptain.updateDayNote(date, "This is a note.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is a note.");
        Map<LocalDate, Day> days = dayCaptain.getWeek(YearWeek.from(date)).days;
        days.values().forEach(d -> assertThat(d.note).isNull());
    }

    @Test
    void week_resource_param_returns_daily_note() {
        dayCaptain.updateDayNote(date, "This is a note.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is a note.");
        Map<LocalDate, Day> days = dayCaptain.getWeekWithDailyNotes(YearWeek.from(date)).days;
        assertThat(days.get(date).note).isEqualTo("This is a note.");
        days.forEach((key, value) -> {
            if (!key.equals(date))
                assertThat(value.note).isNull();
        });
    }

    @BeforeEach
    void setUp() {
        dayCaptain.createDayTask("Empty", date);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.updateDayNote(date, null);
        dayCaptain.deleteDayTasks(date);
    }

}
