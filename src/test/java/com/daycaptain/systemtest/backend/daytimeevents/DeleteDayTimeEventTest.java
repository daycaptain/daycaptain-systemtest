package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// the name of the recurring events in the test db needs to be "Recurring event"
//   and be set on 2021-07-09 and following
// if undo fails, db needs more time to collect the schema information
public class DeleteDayTimeEventTest {

    private static final LocalDate date = LocalDate.of(2021, 7, 9);

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private String actionId;

    @Test
    void delete_single_recurring_event() {
        Day day = dayCaptain.getDay(date);
        DayTimeEvent event = day.timeEvents.get(0);
        assertThat(event.recurring).isTrue();
        actionId = dayCaptain.deleteDayTimeEvent(event);

        day = dayCaptain.getDay(date);
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");

        day = dayCaptain.getDay(date.plusDays(1));
        assertThat(day.timeEvents).extracting(e -> e.string).contains("Recurring event");

        day = dayCaptain.getDay(date.plusDays(2));
        assertThat(day.timeEvents).extracting(e -> e.string).contains("Recurring event");
    }

    @Test
    void delete_all_following_recurring_events() {
        LocalDate tomorrow = DeleteDayTimeEventTest.date.plusDays(1);
        Day day = dayCaptain.getDay(tomorrow);
        DayTimeEvent event = day.timeEvents.get(0);
        assertThat(event.recurring).isTrue();
        actionId = dayCaptain.deleteDayTimeEvent(event, "following");

        day = dayCaptain.getDay(date);
        assertThat(day.timeEvents).extracting(e -> e.string).contains("Recurring event");

        day = dayCaptain.getDay(tomorrow);
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");

        day = dayCaptain.getDay(tomorrow.plusDays(1));
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");
    }

    @Test
    void delete_all_recurring_events() {
        LocalDate tomorrow = DeleteDayTimeEventTest.date.plusDays(1);
        Day day = dayCaptain.getDay(tomorrow);
        DayTimeEvent event = day.timeEvents.get(0);
        assertThat(event.recurring).isTrue();
        actionId = dayCaptain.deleteDayTimeEvent(event, "all");

        day = dayCaptain.getDay(date);
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");

        day = dayCaptain.getDay(tomorrow);
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");

        day = dayCaptain.getDay(tomorrow.plusDays(1));
        assertThat(day.timeEvents).extracting(e -> e.string).doesNotContain("Recurring event");
    }

    @AfterEach
    void cleanUp() {
        if (actionId != null)
            dayCaptain.undo(actionId);
    }

}
