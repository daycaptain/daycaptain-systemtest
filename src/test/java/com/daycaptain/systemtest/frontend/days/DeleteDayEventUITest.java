package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditDayEventAction;
import com.daycaptain.systemtest.frontend.elements.DayEventList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDayEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private DayEventList events;

    @BeforeEach
    void beforeEach() {
        system.deleteDayEvents(date);

        events = dayCaptain.day(date).dayEvents();
        events.createSave("New event");
        events.createSave("Another event");
        events.createSave("Third event");
    }

    @Test
    void delete_in_list() {
        events.delete(1);

        // events are sorted by name
        assertThat(events.getNames()).containsExactly("Another event", "Third event");
    }

    @Test
    void delete_in_edit_action_click() {
        EditDayEventAction edit = events.edit(1);

        edit.clickDelete();
        assertThat(events.getNames()).containsExactly("Another event", "Third event");
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
