package com.daycaptain.systemtest.backend.projects;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Project;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void clearTestObjects() {
        dayCaptain.deleteProjects("My Project 1");
        dayCaptain.deleteProjects("My Project 2");
        dayCaptain.deleteAreas("Area 1");
    }

    @Test
    void testCreateProjectFromName() {
        URI itemId = dayCaptain.createProject("My Project 1");
        assertThat(itemId).isNotNull();

        Project project = dayCaptain.getProject(itemId);
        assertThat(project._self).isEqualTo(itemId);
        assertThat(project.string).isEqualTo("My Project 1");
        assertThat(project.area).isNull();
        assertThat(project.note).isNull();
        assertThat(project.archived).isFalse();
    }

    @Test
    void testCreateProjectWithEqualName() {
        URI itemId = dayCaptain.createProject("My Project 1");
        assertThat(itemId).isNotNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createProject("My Project 1"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testGetProjects() {
        URI itemId1 = dayCaptain.createProject("My Project 1");
        assertThat(itemId1).isNotNull();
        Project project1 = dayCaptain.getProject(itemId1);
        assertThat(project1._self).isEqualTo(itemId1);

        URI itemId2 = dayCaptain.createProject("My Project 2");
        assertThat(itemId2).isNotNull();
        Project project2 = dayCaptain.getProject(itemId2);
        assertThat(project2._self).isEqualTo(itemId2);

        assertThat(dayCaptain.getProjects(false).stream().map(p -> p._self)).contains(project1._self, project2._self);
    }

    @Test
    @Disabled
    void testDeleteUnarchivedProject() {
        URI itemId = dayCaptain.createProject("My Project 1");
        assertThat(itemId).isNotNull();

        Project project = dayCaptain.getProject(itemId);

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.deleteProject(project));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testDeleteArchivedProject() {
        URI itemId = dayCaptain.createProject("My Project 1");
        assertThat(itemId).isNotNull();

        Project project = dayCaptain.getProject(itemId);
        dayCaptain.updateProject(project, "archived", true);
        dayCaptain.deleteProject(project);

        assertThat(dayCaptain.getProjects(false).stream().map(p -> p._self)).doesNotContain(project._self);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.getProject(itemId));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 404");
    }

    @Test
    void testCreateProjectWithEmptyName() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createProject(""));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testCreateProjectWithSameNameAsArchived() {
        URI itemId1 = dayCaptain.createProject("My Project 1");
        Project project1 = dayCaptain.getProject(itemId1);
        dayCaptain.updateProject(project1, "archived", true);

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createProject("My Project 1"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testCreateProjectWithArea() {
        URI area = dayCaptain.createArea("Area 1");
        URI itemId = dayCaptain.createProjectWithArea("My Project 1", "Area 1");
        assertThat(itemId).isNotNull();

        Project project = dayCaptain.getProject(itemId);
        assertThat(project.area).isEqualTo("Area 1");
    }

    @Test
    void testCreateProjectWithInvalidArea() {
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createProjectWithArea("My Project 1", "Invalid Area"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testCreateProjectWithNote() {
        URI itemId = dayCaptain.createProjectWithNote("My Project 1", "A project note.");

        Project project = dayCaptain.getProject(itemId);
        assertThat(project.note).isEqualTo("A project note.");
    }

    @Test
    void testUpdateProjectArea() {
        URI area = dayCaptain.createArea("Area 1");
        URI itemId = dayCaptain.createProjectWithNote("My Project 1", "A project note.");
        Project project = dayCaptain.getProject(itemId);

        dayCaptain.updateProject(project, "area", "Area 1");
        Project updated = dayCaptain.getProject(itemId);
        assertThat(updated.area).isEqualTo("Area 1");
        assertThat(updated.note).isEqualTo("A project note.");
        assertThat(updated.archived).isFalse();
    }

    @Test
    void testUpdateProjectArchived() {
        URI itemId = dayCaptain.createProject("My Project 1");
        Project project = dayCaptain.getProject(itemId);

        dayCaptain.updateProject(project, "archived", true);
        List<Project> projects = dayCaptain.getProjects(true);
        assertThat(projects.stream().map(p -> p._self)).contains(itemId);

        List<Project> unarchived = dayCaptain.getProjects(false);
        assertThat(unarchived.stream().map(p -> p._self)).doesNotContain(itemId);

        dayCaptain.updateProject(project, "string", "My Project 2");
        project = dayCaptain.getProject(itemId);
        assertThat(project.string).isEqualTo("My Project 2");
        assertThat(project.archived).isTrue();
        assertThat(project.area).isNull();
        assertThat(project.note).isNull();
    }

    @Test
    void testUpdateProjectName() {
        URI itemId = dayCaptain.createProject("My Project 1");
        Project project = dayCaptain.getProject(itemId);

        dayCaptain.updateProject(project, "string", "My Project 2");

        Project updated = dayCaptain.getProject(itemId);
        assertThat(updated.string).isEqualTo("My Project 2");

        List<Project> projects = dayCaptain.getProjects(false);
        assertThat(projects.stream().map(p -> p.string)).doesNotContain("My Project 1");
    }

    @Test
    void testUpdateProjectSameName() {
        URI itemId = dayCaptain.createProject("My Project 1");
        Project project = dayCaptain.getProject(itemId);

        dayCaptain.updateProject(project, "string", "My Project 1");

        Project updated = dayCaptain.getProject(itemId);
        assertThat(updated.string).isEqualTo("My Project 1");

        List<Project> projects = dayCaptain.getProjects(false);
        assertThat(projects.stream().map(p -> p.string)).contains("My Project 1");
    }

    @Test
    void testUpdateProjectNameWithConflict() {
        URI itemId1 = dayCaptain.createProject("My Project 1");

        URI itemId2 = dayCaptain.createProject("My Project 2");
        Project project2 = dayCaptain.getProject(itemId2);

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateProject(project2, "string", "My Project 1"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
