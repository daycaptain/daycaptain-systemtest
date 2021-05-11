package com.daycaptain.systemtest.backend.projects;

import com.daycaptain.systemtest.Times;
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

public class MigrateProjectToAreaTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void migrate_project_appears_in_lists() {
        URI projectId = dayCaptain.createProject("Test project");
        Project project = dayCaptain.getProject(projectId);
        assertThat(dayCaptain.getProjects(false)).extracting(b -> b.string)
                .contains("Business idea", "Test project");
        assertThat(dayCaptain.getAreas()).extracting(p -> p.name)
                .doesNotContain("Test project");

        dayCaptain.migrateProjectToArea(project);

        assertThat(dayCaptain.getProjects(false)).extracting(b -> b.string)
                .doesNotContain("Test project");
        assertThat(dayCaptain.getAreas()).extracting(p -> p.name)
                .contains("Test project");
    }

    @Test
    void migrate_project_area_complete_data() {
        URI projectId = dayCaptain.createProject("Test project");
        Project project = dayCaptain.getProject(projectId);
        dayCaptain.migrateProjectToArea(project);

        Area area = CollectionUtils.findArea(dayCaptain.getAreas(), "Test project");
        assertThat(area.keyCode).isEqualTo("T");
        assertThat(area.color).matches("^#[0-9a-f]{6}");
        assertThat(area.order).isGreaterThan(2);
    }

    @Test
    void migrate_archived_project_appears_in_lists() {
        URI projectId = dayCaptain.createProject("Test project");
        Project project = dayCaptain.getProject(projectId);
        dayCaptain.updateProject(project, "archived", true);
        assertThat(dayCaptain.getProjects(true)).extracting(b -> b.string)
                .contains("Test project");
        assertThat(dayCaptain.getAreas()).extracting(p -> p.name)
                .doesNotContain("Test project");

        dayCaptain.migrateProjectToArea(project);

        assertThat(dayCaptain.getProjects(true)).extracting(b -> b.string)
                .doesNotContain("Test project");
        assertThat(dayCaptain.getAreas()).extracting(p -> p.name)
                .contains("Test project");
        assertThat(CollectionUtils.findArea(dayCaptain.getAreas(), "Test project").archived).isTrue();
    }

    @Test
    void migrate_project_connections_stay_intact() {
        Project project = dayCaptain.getProject(dayCaptain.createProject("Test project"));

        LocalDate date = LocalDate.of(2021, 1, 1);
        URI timeEvent = dayCaptain.createDayTimeEventWithProject("Time event", Times.time(date, 12), Times.time(date, 14), "Test project");
        URI dayTask = dayCaptain.createDayTaskWithProject("Day task", date, 0, "Test project");
        URI weekTask = dayCaptain.createWeekTaskWithProject("Week task", YearWeek.from(date), 0, "Test project");
        URI inboxItem = dayCaptain.createInboxItemWithProject("Backlog item", "Test project");
        URI backlogItem = dayCaptain.createBacklogItemWithProject("Backlog item", dayCaptain.getBacklogs().get(1), "Test project");

        dayCaptain.migrateProjectToArea(project);

        assertThat(dayCaptain.getDayTimeEvent(timeEvent).relatedProject).isNull();
        assertThat(dayCaptain.getDayTimeEvent(timeEvent).area).isEqualTo("Test project");
        assertThat(dayCaptain.getTask(dayTask).area).isEqualTo("Test project");
        assertThat(dayCaptain.getTask(weekTask).area).isEqualTo("Test project");
        assertThat(dayCaptain.getBacklogItem(inboxItem).area).isEqualTo("Test project");
        assertThat(dayCaptain.getBacklogItem(backlogItem).area).isEqualTo("Test project");
    }

    @Test
    void cannot_migrate_project_area_with_same_name_exists() {
        Project project = dayCaptain.getProject(dayCaptain.createProject("Test project"));
        dayCaptain.getArea(dayCaptain.createArea("Test project"));

        AssertionError error = assertThrows(AssertionError.class, () -> dayCaptain.migrateProjectToArea(project));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void migrate_project_to_area_area_connection_gone() {
        dayCaptain.getArea(dayCaptain.createArea("Test area"));
        Project project = dayCaptain.getProject(dayCaptain.createProjectWithArea("Test project", "Test area"));
        assertThat(project.area).isEqualTo("Test area");

        dayCaptain.migrateProjectToArea(project);
        dayCaptain.migrateAreaToProject(CollectionUtils.findArea(dayCaptain.getAreas(), "Test project"));

        project = CollectionUtils.findProject(dayCaptain.getProjects(false), "Test project");
        assertThat(project.area).isNull();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteProjects("Test project");
        dayCaptain.deleteAreas("Test project", "Test area");
        dayCaptain.deleteBacklogItemsInAllBacklogs("Backlog item");
    }

}
