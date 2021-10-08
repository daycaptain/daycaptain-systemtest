package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.*;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findDayTimeEvent;
import static com.daycaptain.systemtest.backend.CollectionUtils.findTask;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTimeEventTest {

    public static final LocalDate date = LocalDate.of(2020, 5, 9);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testCreateDayTimeEvent() {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event", start, end);
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event");
    }

    @Test
    void testCreateDayTimeEventDateTimeOutOfRange() {
        LocalDateTime start = LocalDateTime.of(date.plusDays(1), LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date.plusDays(1), LocalTime.of(11, 0));
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTimeEvent("Invalid event", date, start, end));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testCreateDayTimeEventAssignedFromDayTask() {
        URI taskId = dayCaptain.createDayTask("Reading", date);
        Task task = findTask(dayCaptain.getDay(date).tasks, taskId);
        int assignedEvents = task.assignedDayTimeEvents.size();

        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(10, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(11, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event, from day task", start, end, task);
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event, from day task");
        assertThat(event.assignedFromTask).isEqualTo(task._self);

        task = findTask(dayCaptain.getDay(date).tasks, task._self);
        assertThat(task.assignedDayTimeEvents).hasSize(assignedEvents + 1);
    }

    @Test
    void testCreateDayTimeEventAssignedFromWeekTask() {
        Task task = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Something");
        int assignedEvents = task.assignedDayTimeEvents.size();

        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(12, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(14, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event, from week task", start, end, task);
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        DayTimeEvent event = CollectionUtils.findDayTimeEvent(events, eventId);
        assertThat(event.string).isEqualTo("New event, from week task");
        assertThat(event.assignedFromTask).isEqualTo(task._self);

        task = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, task._self);
        assertThat(task.assignedDayTimeEvents).hasSize(assignedEvents + 1);
    }

    @Test
    void testCreateDayTimeEventAssignedFromRelatedProject() {
        Task task = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Working on my project");
        int assignedEvents = task.assignedDayTimeEvents.size();

        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(12, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(14, 0));
        URI eventId = dayCaptain.createDayTimeEvent("New event, from week task", start, end, task);
        assertThat(eventId).isNotNull();

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event, from week task");
        assertThat(event.assignedFromTask).isEqualTo(task._self);
        assertThat(event.project).isNull();
        assertThat(event.area).isNull();
        assertThat(event.relatedProject).isEqualTo("Business idea");
        assertThat(event.relatedArea).isEqualTo("Business");

        task = findTask(dayCaptain.getWeek(YearWeek.of(2020, 19)).tasks, "Working on my project");
        assertThat(task.assignedDayTimeEvents).hasSize(assignedEvents + 1);
    }

    @Test
    void testCreateDayTimeEventWithArea() {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(12, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(14, 0));
        URI eventId = dayCaptain.createDayTimeEventWithArea("New event, with area", start, end, "IT work");
        assertThat(eventId).isNotNull();

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event, with area");
        assertThat(event.assignedFromTask).isNull();
        assertThat(event.project).isNull();
        assertThat(event.area).isEqualTo("IT work");
        assertThat(event.relatedProject).isNull();
        assertThat(event.relatedArea).isEqualTo("IT work");
    }

    @Test
    void testCreateDayTimeEventWithProject() {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(12, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(14, 0));
        URI eventId = dayCaptain.createDayTimeEventWithProject("New event, with project", start, end, "Business idea");
        assertThat(eventId).isNotNull();

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event, with project");
        assertThat(event.assignedFromTask).isNull();
        assertThat(event.project).isEqualTo("Business idea");
        assertThat(event.area).isNull();
        assertThat(event.relatedProject).isEqualTo("Business idea");
        assertThat(event.relatedArea).isEqualTo("Business");
    }

    @Test
    void testCreateDayTimeEventWithNote() {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(12, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(14, 0));
        URI eventId = dayCaptain.createDayTimeEventWithNote("New event, with note", start, end, "A note");
        assertThat(eventId).isNotNull();

        DayTimeEvent event = findDayTimeEvent(dayCaptain.getDay(date).timeEvents, eventId);
        assertThat(event.string).isEqualTo("New event, with note");
        assertThat(event.note).isEqualTo("A note");
        assertThat(event.assignedFromTask).isNull();
        assertThat(event.project).isNull();
        assertThat(event.area).isNull();
        assertThat(event.relatedProject).isNull();
        assertThat(event.relatedArea).isNull();
    }

    @Test
    void testCreateInvalidStartAfterEnd() {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(14, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(12, 0));
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTimeEvent("New event", start, end));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testCreateInvalidStartDateAfterEnd() {
        LocalDateTime start = LocalDateTime.of(date.plusDays(1), LocalTime.of(11, 0));
        LocalDateTime end = LocalDateTime.of(date, LocalTime.of(12, 0));
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createDayTimeEvent("New event", start, end));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
