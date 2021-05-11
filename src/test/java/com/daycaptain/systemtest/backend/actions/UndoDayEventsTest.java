package com.daycaptain.systemtest.backend.actions;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class UndoDayEventsTest {

    private static final LocalDate DATE = LocalDate.of(2020, 12, 2);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUndoEventDeletion() {
        LocalDate date = DATE;
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(LocalDate.parse(event.start)).isEqualTo(date);

        String actionId = dayCaptain.deleteDayEvent(event);
        events = dayCaptain.getDay(date).dayEvents;
        Assertions.assertThat(CollectionUtils.findDayEvent(events, eventId)).isNull();

        dayCaptain.undo(actionId);

        events = dayCaptain.getDay(date).dayEvents;
        event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(LocalDate.parse(event.start)).isEqualTo(date);
    }

    @Test
    void testUndoEventUpdate() {
        LocalDate date = DATE;
        LocalDate end = date.plusDays(1);
        URI eventId = dayCaptain.createDayEvent("New event", date, end);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(LocalDate.parse(event.start)).isEqualTo(date);

        String actionId = dayCaptain.updateDayEvent(event, "start", date, "end", date);

        events = dayCaptain.getDay(date).dayEvents;
        event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(LocalDate.parse(event.start)).isEqualTo(date);
        assertThat(LocalDate.parse(event.end)).isEqualTo(date);

        dayCaptain.undo(actionId);

        events = dayCaptain.getDay(date).dayEvents;
        event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(LocalDate.parse(event.start)).isEqualTo(date);
        assertThat(LocalDate.parse(event.end)).isEqualTo(end);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayEvents(DATE);
    }

}
