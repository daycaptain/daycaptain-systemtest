package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekNoteUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);

    @Test
    void create_week_note() {
        WeekView week = dayCaptain.week(WeekNoteUITest.week);
        assertThat(week.getPageNote()).isEmpty();
        week.updatePageNote("This is a note.");
        assertThat(week.getPageNote()).isEqualTo("This is a note.");
        // double-check
        week.nextWeek();
        week.previousWeek();
        assertThat(week.getPageNote()).isEqualTo("This is a note.");
    }

    @Test
    void update_week_note() {
        WeekView week = dayCaptain.week(WeekNoteUITest.week);
        week.updatePageNote("This is a note.");
        assertThat(week.getPageNote()).isEqualTo("This is a note.");
        week.updatePageNote("This is another note.\n\nWith line breaks.");
        assertThat(week.getPageNote()).isEqualTo("This is another note.\n\nWith line breaks.");
    }

    @Test
    void remove_week_note() {
        WeekView week = dayCaptain.week(WeekNoteUITest.week);
        week.updatePageNote("This is a note.");
        assertThat(week.getPageNote()).isEqualTo("This is a note.");
        week.updatePageNote("");
        assertThat(week.getPageNote()).isEmpty();
    }

    @Test
    @Disabled("Day & week note links are not supported yet")
    void week_note_with_links() {
        throw new UnsupportedOperationException();
    }

    @BeforeEach
    void beforeEach() {
        system.updateWeekNote(week, null);
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
