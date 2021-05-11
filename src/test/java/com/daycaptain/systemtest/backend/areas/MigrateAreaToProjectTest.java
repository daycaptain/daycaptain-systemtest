package com.daycaptain.systemtest.backend.areas;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Area;
import com.daycaptain.systemtest.backend.entity.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;

import static com.daycaptain.systemtest.Times.time;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MigrateAreaToProjectTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void migrate_area_appears_in_lists() {
        URI areaId = dayCaptain.createArea("Test area");
        Area area = dayCaptain.getArea(areaId);
        assertThat(dayCaptain.getAreas()).extracting(b -> b.name)
                .contains("IT work", "Self-improvement", "Business", "Test area");
        assertThat(dayCaptain.getProjects(false)).extracting(p -> p.string)
                .doesNotContain("Test area");

        dayCaptain.migrateAreaToProject(area);

        assertThat(dayCaptain.getAreas()).extracting(b -> b.name)
                .doesNotContain("Test area");
        assertThat(dayCaptain.getProjects(false)).extracting(p -> p.string)
                .contains("Test area");
    }

    @Test
    void migrate_archived_area_appears_in_lists() {
        URI areaId = dayCaptain.createArea("Test area");
        Area area = dayCaptain.getArea(areaId);
        dayCaptain.updateArea(area, "archived", true);
        assertThat(dayCaptain.getAreas()).extracting(b -> b.name)
                .contains("IT work", "Self-improvement", "Business", "Test area");
        assertThat(dayCaptain.getProjects(false)).extracting(p -> p.string)
                .doesNotContain("Test area");

        dayCaptain.migrateAreaToProject(area);

        assertThat(dayCaptain.getAreas()).extracting(b -> b.name)
                .doesNotContain("Test area");
        assertThat(dayCaptain.getProjects(false)).extracting(p -> p.string)
                .doesNotContain("Test area");
        assertThat(dayCaptain.getProjects(true)).extracting(p -> p.string)
                .contains("Test area");
    }

    @Test
    void migrate_area_connections_stay_intact() {
        Area area = dayCaptain.getArea(dayCaptain.createArea("Test area"));

        LocalDate date = LocalDate.of(2021, 1, 1);
        URI timeEvent = dayCaptain.createDayTimeEventWithArea("Time event", time(date, 12), time(date, 14), "Test area");
        URI dayTask = dayCaptain.createDayTaskWithArea("Day task", date, "Test area");
        URI weekTask = dayCaptain.createWeekTaskWithArea("Week task", YearWeek.from(date), 0, "Test area");
        URI inboxItem = dayCaptain.createInboxItemWithArea("Backlog item", "Test area");
        URI backlogItem = dayCaptain.createBacklogItemWithArea("Backlog item", dayCaptain.getBacklogs().get(1), "Test area");

        dayCaptain.migrateAreaToProject(area);

        assertThat(dayCaptain.getDayTimeEvent(timeEvent).relatedArea).isNull();
        assertThat(dayCaptain.getDayTimeEvent(timeEvent).project).isEqualTo("Test area");
        assertThat(dayCaptain.getTask(dayTask).project).isEqualTo("Test area");
        assertThat(dayCaptain.getTask(weekTask).project).isEqualTo("Test area");
        assertThat(dayCaptain.getBacklogItem(inboxItem).project).isEqualTo("Test area");
        assertThat(dayCaptain.getBacklogItem(backlogItem).project).isEqualTo("Test area");
    }

    @Test
    void cannot_migrate_area_project_with_same_name_exists() {
        Area area = dayCaptain.getArea(dayCaptain.createArea("Test area"));
        dayCaptain.createProject("Test area");

        AssertionError error = assertThrows(AssertionError.class, () -> dayCaptain.migrateAreaToProject(area));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void migrate_area_to_project_project_connection_gone() {
        Area area = dayCaptain.getArea(dayCaptain.createArea("Test area"));
        URI projectId = dayCaptain.createProjectWithArea("Test project", "Test area");
        assertThat(dayCaptain.getProject(projectId).area).isEqualTo("Test area");

        dayCaptain.migrateAreaToProject(area);
        Project project = CollectionUtils.findProject(dayCaptain.getProjects(false), "Test area");
        assertThat(dayCaptain.getProject(projectId).area).isNull();

        dayCaptain.migrateProjectToArea(project);
        assertThat(dayCaptain.getProject(projectId).area).isNull();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteProjects("Test area", "Test project");
        dayCaptain.deleteAreas("Test area");
        dayCaptain.deleteBacklogItemsInAllBacklogs("Backlog item");
    }

}
