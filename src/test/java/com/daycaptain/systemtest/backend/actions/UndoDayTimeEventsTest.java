package com.daycaptain.systemtest.backend.actions;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findDayTimeEvent;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class UndoDayTimeEventsTest {

    private static final LocalDate DATE = LocalDate.of(2020, 12, 2);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUndoTimeEventDeletion() {
        LocalDate date = DATE;
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(ZonedDateTime.parse(event.start).toLocalDateTime()).isEqualTo(start.toString());

        String actionId = dayCaptain.deleteDayTimeEvent(event);
        events = dayCaptain.getDay(date).timeEvents;
        assertThat(CollectionUtils.findDayTimeEvent(events, eventId)).isNull();

        dayCaptain.undo(actionId);

        events = dayCaptain.getDay(date).timeEvents;
        event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(ZonedDateTime.parse(event.start).toLocalDateTime()).isEqualTo(start.toString());
    }

    @Test
    void testUndoTimeEventUpdate() {
        LocalDate date = DATE;
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(ZonedDateTime.parse(event.start).toLocalDateTime()).isEqualTo(start.toString());

        ZonedDateTime newStart = ZonedDateTime.parse(event.start).plusHours(1);
        ZonedDateTime newEnd = ZonedDateTime.parse(event.end).plusHours(1);
        String actionId = dayCaptain.updateDayTimeEvent(event, "start", newStart, "end", newEnd);

        events = dayCaptain.getDay(date).timeEvents;
        event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(ZonedDateTime.parse(event.start).toLocalDateTime()).isEqualTo(start.plusHours(1).toString());

        dayCaptain.undo(actionId);

        events = dayCaptain.getDay(date).timeEvents;
        event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(ZonedDateTime.parse(event.start).toLocalDateTime()).isEqualTo(start.toString());
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTasks(DATE);
    }

}
