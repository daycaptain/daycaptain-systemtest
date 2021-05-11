package com.daycaptain.systemtest.backend.daytimeevents;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.DayTimeEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListDayTimeEventTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testListDayTimeEventsSorted() {
        LocalDate date = LocalDate.of(2020, 5, 11);
        URI eventId = dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)));
        assertThat(eventId).isNotNull();

        eventId = dayCaptain.createDayTimeEvent("New event, another", LocalDateTime.of(date, LocalTime.of(11, 0)), LocalDateTime.of(date, LocalTime.of(12, 0)));
        assertThat(eventId).isNotNull();

        eventId = dayCaptain.createDayTimeEvent("New event, yet another", LocalDateTime.of(date, LocalTime.of(12, 0)), LocalDateTime.of(date, LocalTime.of(14, 0)));
        assertThat(eventId).isNotNull();

        List<DayTimeEvent> events = dayCaptain.getDay(date).timeEvents;
        assertThat(events.size()).isGreaterThanOrEqualTo(3);

        assertThat(events).isSortedAccordingTo(Comparator.comparing(event -> event.start));

        events = dayCaptain.getWeek(YearWeek.from(date)).days.get(date).timeEvents;
        assertThat(events.size()).isGreaterThanOrEqualTo(3);

        assertThat(events).isSortedAccordingTo(Comparator.comparing(event -> event.start));
    }

    @Test
    void testListFilteredTimeEvents() {
        LocalDate date = LocalDate.of(2020, 9, 29);
        dayCaptain.createDayTimeEventWithArea("New event, area", LocalDateTime.of(date, LocalTime.of(10, 0)), LocalDateTime.of(date, LocalTime.of(11, 0)), "IT work");
        dayCaptain.createDayTimeEvent("New event", LocalDateTime.of(date, LocalTime.of(11, 0)), LocalDateTime.of(date, LocalTime.of(12, 0)));
        dayCaptain.createDayTimeEventWithArea("New event, another area", LocalDateTime.of(date, LocalTime.of(13, 0)), LocalDateTime.of(date, LocalTime.of(14, 0)), "Business");

        assertThat(dayCaptain.getDay(date).timeEvents).hasSize(3);
        assertThat(dayCaptain.getDay(date, "IT work").timeEvents).extracting(e -> e.string).containsExactly("New event, area");
        assertThat(dayCaptain.getDay(date, "Business").timeEvents).extracting(e -> e.string).containsExactly("New event, another area");
        assertThat(dayCaptain.getDay(date, "Self-improvement").timeEvents).isEmpty();
        assertThat(dayCaptain.getDay(date, "unknown").timeEvents).isEmpty();

        YearWeek week = YearWeek.from(date);
        assertThat(dayCaptain.getWeek(week).days.get(date).timeEvents).hasSize(3);
        assertThat(dayCaptain.getWeek(week, "IT work").days.get(date).timeEvents).extracting(e -> e.string).containsExactly("New event, area");
        assertThat(dayCaptain.getWeek(week, "Business").days.get(date).timeEvents).extracting(e -> e.string).containsExactly("New event, another area");
        assertThat(dayCaptain.getWeek(week, "Self-improvement").days.getOrDefault(date, new Day()).timeEvents).isEmpty();
        assertThat(dayCaptain.getWeek(week, "unknown").days.getOrDefault(date, new Day()).timeEvents).isEmpty();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTimeEvents(LocalDate.of(2020, 5, 11));
        dayCaptain.deleteDayTimeEvents(LocalDate.of(2020, 9, 29));
    }

}
