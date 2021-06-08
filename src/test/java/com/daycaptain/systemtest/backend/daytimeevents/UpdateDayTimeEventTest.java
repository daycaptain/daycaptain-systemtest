package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static com.daycaptain.systemtest.Times.time;
import static com.daycaptain.systemtest.backend.CollectionUtils.findDayTimeEvent;
import static org.assertj.core.api.Assertions.assertThat;

public class UpdateDayTimeEventTest {

    private static final LocalDate DATE = LocalDate.of(2020, 5, 9);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUpdateString() {
        LocalDate date = DATE;
        URI eventId = dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event");

        dayCaptain.updateDayTimeEvent(event, "string", "Very new event");

        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.string).isEqualTo("Very new event");
    }

    @Test
    void testAddProject() {
        LocalDate date = DATE;
        URI eventId = dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isNull();

        dayCaptain.updateDayTimeEvent(event, "project", "Business idea");

        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testUpdateProject() {
        LocalDate date = DATE;
        URI eventId = dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        dayCaptain.updateDayTimeEvent(event, "project", "Business idea");

        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.relatedProject).isEqualTo("Business idea");

        dayCaptain.updateDayTimeEvent(event, "project", "Work presentations");
        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.project).isEqualTo("Work presentations");
        assertThat(event.relatedProject).isEqualTo("Work presentations");
    }

    @Test
    void testRemoveProject() {
        LocalDate date = DATE;
        URI eventId = dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        dayCaptain.updateDayTimeEvent(event, "project", "Business idea");

        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.relatedProject).isEqualTo("Business idea");

        dayCaptain.updateDayTimeEvent(event, "project", null);
        event = dayCaptain.getDayTimeEvent(event._self);
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isNull();
    }

    @Test
    void testUpdateInvalidStartAfterEndTime() {
        LocalDate date = DATE;
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        ZonedDateTime newTime = ZonedDateTime.parse(event.start).plusDays(1);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateDayTimeEvent(event, "start", newTime));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        DayTimeEvent loaded = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(loaded.string).isEqualTo("New event");
        assertThat(ZonedDateTime.parse(loaded.start).toLocalDateTime()).isEqualTo(start.toString());
    }

    @Test
    void testUpdateInvalidEndBeforeStartTime() {
        LocalDate date = DATE;
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);
        assertThat(eventId).isNotNull();

        DayTimeEvent event = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        ZonedDateTime newTime = ZonedDateTime.parse(event.end).minusDays(1);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateDayTimeEvent(event, "end", newTime));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");

        DayTimeEvent loaded = CollectionUtils.findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(loaded.string).isEqualTo("New event");
        assertThat(ZonedDateTime.parse(loaded.end).toLocalDateTime()).isEqualTo(end.toString());
    }

    @Test
    void testSetProjectRemovesArea() {
        URI eventId = dayCaptain.createDayTimeEventWithArea("New event", time(DATE, 10), time(DATE, 11), "IT work");

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(DATE).timeEvents, eventId);
        assertThat(event.area).isEqualTo("IT work");
        assertThat(event.relatedArea).isEqualTo("IT work");

        dayCaptain.updateDayTimeEvent(event, "project", "Business idea");
        event = findDayTimeEvent(dayCaptain.getDay(DATE).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("Business");
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testSetAreaRemovesProject() {
        URI eventId = dayCaptain.createDayTimeEventWithProject("New event", time(DATE, 10), time(DATE, 11), "Business idea");

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(DATE).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event");
        assertThat(event.area).isNull();
        assertThat(event.relatedArea).isEqualTo("Business");
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.relatedProject).isEqualTo("Business idea");

        dayCaptain.updateDayTimeEvent(event, "area", "IT work");
        event = findDayTimeEvent(dayCaptain.getDay(DATE).timeEvents, eventId);
        assertThat(event.area).isEqualTo("IT work");
        assertThat(event.relatedArea).isEqualTo("IT work");
        assertThat(event.project).isNull();
        assertThat(event.relatedProject).isNull();
    }

    @Test
    void testCannotSetBothAreaAndProject() {
        URI eventId = dayCaptain.createDayTimeEvent("New event", time(DATE, 10), time(DATE, 11));
        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(DATE).timeEvents, eventId);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateDayTimeEvent(event, "area", "IT work", "project", "Business idea"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
