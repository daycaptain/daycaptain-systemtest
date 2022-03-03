package com.daycaptain.systemtest.backend.migrations;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Day;
import com.daycaptain.systemtest.backend.entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;

class MigrationTest {

    private static final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private static final YearWeek week = YearWeek.of(2021, 6);
    private static final LocalDate date = LocalDate.of(2021, 2, 11);
    private static final Set<URI> uris = new HashSet<>();

    @Test
    void items_migrated_to_area() {
        dayCaptain.migrateItemsToArea(uris, "IT work");

        Day day = dayCaptain.getDay(date);
        List<Task> tasks = day.tasks;
        tasks.forEach(t -> {
            assertThat(t.relatedArea).isEqualTo("IT work");
            assertThat(t.relatedProject).isNull();
        });
        tasks.stream().filter(t -> "Task".equals(t.string))
                .forEach(t -> assertThat(t.area).isEqualTo("IT work"));
        tasks.stream().filter(t -> "Related task".equals(t.string))
                .forEach(t -> assertThat(t.area).isNull());

        day.timeEvents.forEach(e -> {
            assertThat(e.relatedArea).isEqualTo("IT work");
            assertThat(e.relatedProject).isNull();
        });
        day.timeEvents.stream().filter(e -> "Related event".equals(e.string))
                .forEach(e -> assertThat(e.area).isNull());

        tasks = dayCaptain.getWeek(week).tasks;
        tasks.forEach(t -> {
            assertThat(t.relatedArea).isEqualTo("IT work");
            assertThat(t.relatedProject).isNull();
        });
        tasks.stream().filter(t -> "Task".equals(t.string))
                .forEach(t -> assertThat(t.area).isEqualTo("IT work"));
        tasks.stream().filter(t -> "Related task".equals(t.string))
                .forEach(t -> assertThat(t.area).isNull());
    }

    @Test
    void items_migrated_to_project() {
        dayCaptain.migrateItemsToProject(uris, "Spanish");

        Day day = dayCaptain.getDay(date);
        List<Task> tasks = day.tasks;
        tasks.forEach(t -> {
            assertThat(t.relatedArea).isNull();
            assertThat(t.relatedProject).isEqualTo("Spanish");
        });
        tasks.stream().filter(t -> "Task".equals(t.string))
                .forEach(t -> assertThat(t.project).isEqualTo("Spanish"));
        tasks.stream().filter(t -> "Related task".equals(t.string))
                .forEach(t -> assertThat(t.project).isNull());

        day.timeEvents.forEach(e -> {
            assertThat(e.relatedArea).isNull();
            assertThat(e.relatedProject).isEqualTo("Spanish");
        });
        day.timeEvents.stream().filter(e -> "Related event".equals(e.string))
                .forEach(e -> assertThat(e.project).isNull());

        tasks = dayCaptain.getWeek(week).tasks;
        tasks.forEach(t -> {
            assertThat(t.relatedArea).isNull();
            assertThat(t.relatedProject).isEqualTo("Spanish");
        });
        tasks.stream().filter(t -> "Task".equals(t.string))
                .forEach(t -> assertThat(t.project).isEqualTo("Spanish"));
        tasks.stream().filter(t -> "Related task".equals(t.string))
                .forEach(t -> assertThat(t.project).isNull());
    }

    @Test
    void items_related_area_or_project_not_migrated_to_area() {
        Set<URI> uris = new HashSet<>();

        Day day = dayCaptain.getDay(date);
        dayCaptain.getWeek(week).tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> uris.add(t._self));
        day.tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> uris.add(t._self));
        day.timeEvents.stream().filter(e -> e.string.startsWith("Related"))
                .forEach(e -> uris.add(e._self));

        dayCaptain.migrateItemsToArea(uris, "IT work");

        dayCaptain.getWeek(week).tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> {
                    assertThat(t.area).isNull();
                    assertThat(t.relatedArea).isNotEqualTo("IT work");
                });

        day = dayCaptain.getDay(date);
        day.tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> {
                    assertThat(t.area).isNull();
                    assertThat(t.relatedArea).isNotEqualTo("IT work");
                });
        day.timeEvents.stream().filter(e -> e.string.startsWith("Related"))
                .forEach(e -> {
                    assertThat(e.area).isNull();
                    assertThat(e.relatedArea).isNotEqualTo("IT work");
                });
    }

    @Test
    void items_related_area_or_project_not_migrated_to_project() {
        Set<URI> uris = new HashSet<>();

        Day day = dayCaptain.getDay(date);
        dayCaptain.getWeek(week).tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> uris.add(t._self));
        day.tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> uris.add(t._self));
        day.timeEvents.stream().filter(e -> e.string.startsWith("Related"))
                .forEach(e -> uris.add(e._self));

        dayCaptain.migrateItemsToProject(uris, "Spanish");

        dayCaptain.getWeek(week).tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> {
                    assertThat(t.project).isNull();
                    assertThat(t.relatedProject).isNotEqualTo("Spanish");
                });

        day = dayCaptain.getDay(date);
        day.tasks.stream().filter(t -> t.string.startsWith("Related"))
                .forEach(t -> {
                    assertThat(t.project).isNull();
                    assertThat(t.relatedProject).isNotEqualTo("Spanish");
                });
        day.timeEvents.stream().filter(e -> e.string.startsWith("Related"))
                .forEach(e -> {
                    assertThat(e.project).isNull();
                    assertThat(e.relatedProject).isNotEqualTo("Spanish");
                });
    }

    @BeforeEach
    void setUp() {
        uris.add(dayCaptain.createWeekTask("Task", week));
        URI weekTaskWithArea = dayCaptain.createWeekTaskWithArea("Task", week, 0, "Business");
        URI weekTaskWithProject = dayCaptain.createWeekTaskWithProject("Task", week, 0, "Business idea");
        uris.add(weekTaskWithArea);
        uris.add(weekTaskWithProject);
        uris.add(dayCaptain.createDayTask("Related task", date, dayCaptain.getTask(weekTaskWithArea)));
        uris.add(dayCaptain.createDayTask("Related task", date, dayCaptain.getTask(weekTaskWithProject)));
        uris.add(dayCaptain.createDayTask("Task", date));
        URI taskWithArea = dayCaptain.createDayTaskWithArea("Task", date, "Business");
        URI taskWithProject = dayCaptain.createDayTaskWithProject("Task", date, 0, "Business idea");
        uris.add(taskWithArea);
        uris.add(taskWithProject);
        uris.add(dayCaptain.createDayTimeEvent("Related event", time(date, 11), time(date, 12), dayCaptain.getTask(taskWithArea)));
        uris.add(dayCaptain.createDayTimeEvent("Related event", time(date, 10), time(date, 11), dayCaptain.getTask(taskWithProject)));
        uris.add(dayCaptain.createDayTimeEventWithArea("Event", time(date, 10), time(date, 11), "Business"));
        uris.add(dayCaptain.createDayTimeEventWithProject("Event", time(date, 11), time(date, 12), "Business idea"));
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteWeekTasks(week);
        dayCaptain.deleteDayTasks(date);
        dayCaptain.deleteDayTimeEvents(date);
    }

}
