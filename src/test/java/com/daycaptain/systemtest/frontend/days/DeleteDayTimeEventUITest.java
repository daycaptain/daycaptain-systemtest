package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDayTimeEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private DayTimeEventList events;

    @BeforeEach
    void beforeEach() {
        system.deleteDayTimeEvents(date);

        events = dayCaptain.day(date).timeEvents();
        events.createSave("New event", "09:00", "11:00");
        events.createSave("Another event", "12:00", "13:00");
        events.createSave("Third event", "14:00", "15:30");
    }

    @Test
    void delete_in_list() {
        events.delete(1);

        assertThat(events.getNames()).containsExactly("New event", "Third event");
    }

    @Test
    void delete_in_edit_action_click() {
        EditTimeEventAction edit = events.edit(1);

        edit.clickDelete();
        assertThat(events.getNames()).containsExactly("New event", "Third event");
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
