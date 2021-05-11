package com.daycaptain.systemtest.backend.dayevents;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateDayEventTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUpdateString() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        dayCaptain.updateDayEvent(event, "string", "New event, updated");

        events = dayCaptain.getDay(date).dayEvents;
        event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event, updated");
    }

    @Test
    void testUpdateEndDate() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        dayCaptain.updateDayEvent(event, "end", "2020-05-10");

        events = dayCaptain.getDay(date).dayEvents;
        event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.end).isEqualTo("2020-05-10");
    }

    @Test
    void testUpdateDates() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        dayCaptain.updateDayEvent(event, "start", "2020-05-10", "end", "2020-05-11");

        events = dayCaptain.getDay(date).dayEvents;
        org.assertj.core.api.Assertions.assertThat(CollectionUtils.findDayEvent(events, eventId)).isNull();

        events = dayCaptain.getDay(LocalDate.of(2020, 5, 10)).dayEvents;
        String uuid = extractId(eventId);
        event = CollectionUtils.findDayEvent(events, e -> uuid.equals(extractId(e._self)));
        assertThat(event.start).isEqualTo("2020-05-10");
        assertThat(event.end).isEqualTo("2020-05-11");
    }

    @Test
    void testUpdateDatesInvalid() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateDayEvent(event, "start", "2020-05-11", "end", "2020-05-10"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    private static String extractId(URI uri) {
        String path = uri.getRawPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

}
