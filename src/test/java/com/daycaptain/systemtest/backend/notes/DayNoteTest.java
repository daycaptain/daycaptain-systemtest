package com.daycaptain.systemtest.backend.notes;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DayNoteTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void create_day_note() {
        LocalDate date = LocalDate.of(2021, 5, 9);
        assertThat(dayCaptain.getDay(date).note).isNull();
        dayCaptain.updateDayNote(date, "This is a note.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is a note.");
    }

    @Test
    void update_day_note() {
        LocalDate date = LocalDate.of(2021, 5, 9);
        dayCaptain.updateDayNote(date, "This is a note.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is a note.");
        dayCaptain.updateDayNote(date, "This is another note.\n\nWith line breaks.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is another note.\n\nWith line breaks.");
    }

    @Test
    void remove_day_note() {
        LocalDate date = LocalDate.of(2021, 5, 9);
        dayCaptain.updateDayNote(date, "This is a note.");
        assertThat(dayCaptain.getDay(date).note).isEqualTo("This is a note.");
        dayCaptain.updateDayNote(date, null);
        assertThat(dayCaptain.getDay(date).note).isNull();
    }

    @Test
    @Disabled("Day & week note links are not supported yet")
    void day_note_with_links() {
        throw new UnsupportedOperationException();
    }

}
