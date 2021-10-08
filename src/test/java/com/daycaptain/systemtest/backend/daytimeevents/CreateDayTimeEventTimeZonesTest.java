package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static com.daycaptain.systemtest.Times.*;
import static com.daycaptain.systemtest.backend.CollectionUtils.findDayTimeEvent;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTimeEventTimeZonesTest {

    public static final LocalDate date = LocalDate.of(2020, 5, 9);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void create_with_time_zones() {
        ZonedDateTime start = ZonedDateTime.of(date, time(10), berlin);
        ZonedDateTime end = ZonedDateTime.of(date, time(11), berlin);
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-09T11:00:00+02:00[Europe/Berlin]");
    }

    @Test
    void create_time_zone_switch() {
        ZonedDateTime start = ZonedDateTime.of(date, time(10), berlin);
        ZonedDateTime end = ZonedDateTime.of(date, time(12), moscow);
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-09T12:00:00+03:00[Europe/Moscow]");
    }

    @Test
    void event_after_and_before_time_zone_switch_created_with_correct_time_zone() {
        ZonedDateTime start = ZonedDateTime.of(date, time(10), berlin);
        ZonedDateTime end = ZonedDateTime.of(date, time(12), moscow);
        dayCaptain.createDayTimeEvent("New event", start, end);

        URI eventId = dayCaptain.createDayTimeEvent("Later event", LocalDateTime.of(date, time(14)), LocalDateTime.of(date, time(15)));
        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.start).isEqualTo("2020-05-09T14:00:00+03:00[Europe/Moscow]");
        assertThat(event.end).isEqualTo("2020-05-09T15:00:00+03:00[Europe/Moscow]");

        eventId = dayCaptain.createDayTimeEvent("Earlier event", LocalDateTime.of(date, time(8)), LocalDateTime.of(date, time(9)));
        events = dayCaptain.getDay(date).timeEvents;
        event = findDayTimeEvent(events, eventId);
        assertThat(event.start).isEqualTo("2020-05-09T08:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-09T09:00:00+02:00[Europe/Berlin]");
    }

    @Test
    void create_mix_time_zone_local_date_time_start() {
        ZonedDateTime start = ZonedDateTime.of(date, time(10), berlin);
        LocalDateTime end = LocalDateTime.of(date, time(11));
        URI eventId = dayCaptain.createDayTimeEvent("New event", date, start.toString(), end.toString());
        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-09T11:00:00+02:00[Europe/Berlin]");
    }

    @Test
    void create_mix_time_zone_local_date_time_end() {
        LocalDateTime start = LocalDateTime.of(date, time(10));
        ZonedDateTime end = ZonedDateTime.of(date, time(11), berlin);
        URI eventId = dayCaptain.createDayTimeEvent("New event", date, start.toString(), end.toString());
        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-09T11:00:00+02:00[Europe/Berlin]");
    }

    @Test
    void create_with_shortened_times() {
        String start = "2020-05-09T10:00+02:00";
        String end = "2020-05-09T11:00+02:00";
        URI eventId = dayCaptain.createDayTimeEvent("New event", date, start, end);
        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = findDayTimeEvent(events, eventId);
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+02:00");
        assertThat(event.end).isEqualTo("2020-05-09T11:00:00+02:00");
    }

    @Test
    void create_time_zone_switch_invalid_end_earlier_than_start() {
        ZonedDateTime start = ZonedDateTime.of(date, time(10), berlin);
        ZonedDateTime end = ZonedDateTime.of(date, time(10, 30), moscow);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTimeEvent("Invalid event", start, end));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void events_time_zones_are_changed_after_switch() {
        dayCaptain.deleteDayTimeEvents(date.minusDays(2), date.minusDays(1), date, date.plusDays(1), date.plusDays(2), date.plusDays(3));

        URI event1 = dayCaptain.createDayTimeEvent("Event", time(date.minusDays(2), 10, berlin), time(date.minusDays(2), 12, berlin));
        URI event2 = dayCaptain.createDayTimeEvent("Event", time(date.minusDays(1), 10, berlin), time(date.minusDays(1), 12, berlin));
        URI event3 = dayCaptain.createDayTimeEvent("Event", time(date.plusDays(1), 10, berlin), time(date.plusDays(1), 12, berlin));
        URI event4 = dayCaptain.createDayTimeEvent("Event", time(date.plusDays(2), 10, berlin), time(date.plusDays(2), 12, moscow));
        URI event5 = dayCaptain.createDayTimeEvent("Event", time(date.plusDays(3), 10, moscow), time(date.plusDays(3), 12, moscow));

        URI switchEvent = dayCaptain.createDayTimeEvent("Switch", time(date, 10, moscow), time(date, 12, berlin));

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(date.minusDays(2)).timeEvents, event1);
        assertThat(event.start).isEqualTo("2020-05-07T11:00:00+03:00[Europe/Moscow]");
        assertThat(event.end).isEqualTo("2020-05-07T13:00:00+03:00[Europe/Moscow]");

        event = findDayTimeEvent(dayCaptain.getDay(date.minusDays(1)).timeEvents, event2);
        assertThat(event.start).isEqualTo("2020-05-08T11:00:00+03:00[Europe/Moscow]");
        assertThat(event.end).isEqualTo("2020-05-08T13:00:00+03:00[Europe/Moscow]");

        event = findDayTimeEvent(dayCaptain.getDay(date).timeEvents, switchEvent);
        assertThat(event.start).isEqualTo("2020-05-09T10:00:00+03:00[Europe/Moscow]");
        assertThat(event.end).isEqualTo("2020-05-09T12:00:00+02:00[Europe/Berlin]");

        event = findDayTimeEvent(dayCaptain.getDay(date.plusDays(1)).timeEvents, event3);
        assertThat(event.start).isEqualTo("2020-05-10T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-10T12:00:00+02:00[Europe/Berlin]");

        event = findDayTimeEvent(dayCaptain.getDay(date.plusDays(2)).timeEvents, event4);
        assertThat(event.start).isEqualTo("2020-05-11T10:00:00+02:00[Europe/Berlin]");
        assertThat(event.end).isEqualTo("2020-05-11T12:00:00+03:00[Europe/Moscow]");

        event = findDayTimeEvent(dayCaptain.getDay(date.plusDays(3)).timeEvents, event5);
        assertThat(event.start).isEqualTo("2020-05-12T10:00:00+03:00[Europe/Moscow]");
        assertThat(event.end).isEqualTo("2020-05-12T12:00:00+03:00[Europe/Moscow]");

        dayCaptain.deleteDayTimeEvents(date.minusDays(2), date.minusDays(1), date, date.plusDays(1), date.plusDays(2), date.plusDays(3));
    }

    @BeforeEach
    @AfterEach
    void cleanUp() {
        dayCaptain.deleteDayTimeEvents(date);
    }

}
