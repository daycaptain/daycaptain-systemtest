package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DayNoteUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();

    private static final LocalDate date = LocalDate.of(2021, 5, 9);

    @Test
    void create_day_note() {
        DayView day = dayCaptain.day(date);
        assertThat(day.getPageNote()).isEmpty();
        day.updatePageNote("This is a note.");
        assertThat(day.getPageNote()).isEqualTo("This is a note.");
        // double-check
        day.nextDay();
        day.previousDay();
        assertThat(day.getPageNote()).isEqualTo("This is a note.");
    }

    @Test
    void update_day_note() {
        DayView day = dayCaptain.day(date);
        day.updatePageNote("This is a note.");
        assertThat(day.getPageNote()).isEqualTo("This is a note.");
        day.updatePageNote("This is another note.\n\nWith line breaks.");
        assertThat(day.getPageNote()).isEqualTo("This is another note.\n\nWith line breaks.");
    }

    @Test
    void remove_day_note() {
        DayView day = dayCaptain.day(date);
        day.updatePageNote("This is a note.");
        assertThat(day.getPageNote()).isEqualTo("This is a note.");
        day.updatePageNote("");
        assertThat(day.getPageNote()).isEmpty();
    }

    @Test
    @Disabled("Day & week note links are not supported yet")
    void day_note_with_links() {
        throw new UnsupportedOperationException();
    }

    @BeforeEach
    void beforeEach() {
        system.updateDayNote(date, null);
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
