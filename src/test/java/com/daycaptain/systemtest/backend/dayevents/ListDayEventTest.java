package com.daycaptain.systemtest.backend.dayevents;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayEvent;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListDayEventTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testListDayEventsSorted() {
        LocalDate date = LocalDate.of(2020, 5, 9);
        URI eventId = dayCaptain.createDayEvent("New event", date, date);
        assertThat(eventId).isNotNull();

        eventId = dayCaptain.createDayEvent("New event, yet another", date, date);
        assertThat(eventId).isNotNull();

        eventId = dayCaptain.createDayEvent("New event, another", date, date);
        assertThat(eventId).isNotNull();

        List<DayEvent> events = dayCaptain.getDay(date).dayEvents;
        assertThat(events.size()).isGreaterThanOrEqualTo(3);

        assertThat(events).isSortedAccordingTo(Comparator.comparing(event -> event.string));
    }

}
