package com.daycaptain.systemtest.backend.notes;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekNoteTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void create_week_note() {
        YearWeek week = YearWeek.of(2021, 13);
        assertThat(dayCaptain.getWeek(week).note).isNull();
        dayCaptain.updateWeekNote(week, "This is a note.");
        assertThat(dayCaptain.getWeek(week).note).isEqualTo("This is a note.");
    }

    @Test
    void update_week_note() {
        YearWeek week = YearWeek.of(2021, 13);
        dayCaptain.updateWeekNote(week, "This is a note.");
        assertThat(dayCaptain.getWeek(week).note).isEqualTo("This is a note.");
        dayCaptain.updateWeekNote(week, "This is another note.\n\nWith line breaks.");
        assertThat(dayCaptain.getWeek(week).note).isEqualTo("This is another note.\n\nWith line breaks.");
    }

    @Test
    void remove_week_note() {
        YearWeek week = YearWeek.of(2021, 13);
        dayCaptain.updateWeekNote(week, "This is a note.");
        assertThat(dayCaptain.getWeek(week).note).isEqualTo("This is a note.");
        dayCaptain.updateWeekNote(week, null);
        assertThat(dayCaptain.getWeek(week).note).isNull();
    }

    @Test
    @Disabled("Day & week note links are not supported yet")
    void week_note_with_links() {
        throw new UnsupportedOperationException();
    }

}
