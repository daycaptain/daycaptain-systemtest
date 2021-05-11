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

public class CreateDayEventTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testCreateDayTimeEvent() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event, single day", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event, single day");
        assertThat(event.start).isEqualTo(date.toString());
        assertThat(event.end).isEqualTo(date.toString());
    }

    @Test
    void testCreateDayTimeEventMultipleDays() {
        LocalDate start = LocalDate.of(2020, 5, 9);
        LocalDate end = LocalDate.of(2020, 5, 11);
        URI eventId = dayCaptain.createDayEvent("New event, multiple days", start, end);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(start).dayEvents;
        DayEvent event = CollectionUtils.findDayEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event, multiple days");
        assertThat(event.start).isEqualTo(start.toString());
        assertThat(event.end).isEqualTo(end.toString());
    }

    @Test
    void testCreateDayTimeEventDateTimeOutOfRange() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        LocalDate start = LocalDate.of(2020, 5, 11);
        LocalDate end = LocalDate.of(2020, 5, 12);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayEvent("Invalid event", date, start, end));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
