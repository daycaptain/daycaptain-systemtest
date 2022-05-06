package com.daycaptain.systemtest.backend.daytasks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;

public class AssignDayTimeEventFromTaskTest {

    private static final LocalDate DATE = LocalDate.of(2020, 5, 19);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void assign_task_to_day_time_event() {
        URI task = dayCaptain.createDayTask("New day task", DATE);
        URI firstEvent = dayCaptain.createDayTimeEvent("New event", time(DATE, 10), time(DATE, 11));
        URI secondEvent = dayCaptain.createDayTimeEvent("Another event", time(DATE, 12), time(DATE, 13));
        dayCaptain.addRelation(task, firstEvent);

        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactly(firstEvent);

        dayCaptain.addRelation(task, secondEvent);

        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactlyInAnyOrder(firstEvent, secondEvent);

        dayCaptain.removeRelation(task, firstEvent);
        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactlyInAnyOrder(secondEvent);

        dayCaptain.removeRelation(task, secondEvent);
        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).isEmpty();
    }

    @Test
    void assign_day_time_event_to_task() {
        URI task = dayCaptain.createDayTask("New day task", DATE);
        URI firstEvent = dayCaptain.createDayTimeEvent("New event", time(DATE, 10), time(DATE, 11));
        URI secondEvent = dayCaptain.createDayTimeEvent("Another event", time(DATE, 12), time(DATE, 13));
        dayCaptain.addRelation(firstEvent, task);

        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactly(firstEvent);
        assertThat(dayCaptain.getDayTimeEvent(firstEvent).assignedFromTask).isEqualTo(task);

        dayCaptain.addRelation(secondEvent, task);

        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactlyInAnyOrder(firstEvent, secondEvent);
        assertThat(dayCaptain.getDayTimeEvent(secondEvent).assignedFromTask).isEqualTo(task);
        assertThat(dayCaptain.getDayTimeEvent(firstEvent).assignedFromTask).isEqualTo(task);

        dayCaptain.removeRelation(firstEvent, task);
        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).containsExactlyInAnyOrder(secondEvent);

        dayCaptain.removeRelation(secondEvent, task);
        assertThat(dayCaptain.getTask(task).assignedDayTimeEvents).isEmpty();
    }

    @Test
    void reassign_day_time_event_to_task_should_remove_other_link_to_task() {
        URI firstTask = dayCaptain.createDayTask("New task", DATE);
        URI secondTask = dayCaptain.createDayTask("Another task", DATE);
        URI event = dayCaptain.createDayTimeEvent("New event", time(DATE, 10), time(DATE, 11));

        dayCaptain.addRelation(firstTask, event);
        assertThat(dayCaptain.getTask(firstTask).assignedDayTimeEvents).containsExactly(event);
        assertThat(dayCaptain.getDayTimeEvent(event).assignedFromTask).isEqualTo(firstTask);

        dayCaptain.addRelation(secondTask, event);
        assertThat(dayCaptain.getTask(firstTask).assignedDayTimeEvents).isEmpty();
        assertThat(dayCaptain.getTask(secondTask).assignedDayTimeEvents).containsExactly(event);
        assertThat(dayCaptain.getDayTimeEvent(event).assignedFromTask).isEqualTo(secondTask);
    }

    @Test
    void reassign_day_time_event_should_remove_other_link_to_task() {
        URI firstTask = dayCaptain.createDayTask("New task", DATE);
        URI secondTask = dayCaptain.createDayTask("Another task", DATE);
        URI event = dayCaptain.createDayTimeEvent("New event", time(DATE, 10), time(DATE, 11));

        dayCaptain.addRelation(event, firstTask);
        assertThat(dayCaptain.getTask(firstTask).assignedDayTimeEvents).containsExactly(event);
        assertThat(dayCaptain.getDayTimeEvent(event).assignedFromTask).isEqualTo(firstTask);

        dayCaptain.addRelation(event, secondTask);
        assertThat(dayCaptain.getTask(firstTask).assignedDayTimeEvents).isEmpty();
        assertThat(dayCaptain.getTask(secondTask).assignedDayTimeEvents).containsExactly(event);
        assertThat(dayCaptain.getDayTimeEvent(event).assignedFromTask).isEqualTo(secondTask);
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTasks(DATE);
        dayCaptain.deleteDayTimeEvents(DATE);
    }

}
