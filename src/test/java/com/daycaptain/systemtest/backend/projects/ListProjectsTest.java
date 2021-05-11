package com.daycaptain.systemtest.backend.projects;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Project;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListProjectsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testListActiveProjects() {
        List<Project> projects = dayCaptain.getProjects(false);
        assertThat(projects.size()).isGreaterThanOrEqualTo(3);
        assertThat(projects).isSortedAccordingTo(Comparator.comparing(project -> project.string));
        assertThat(projects).allMatch(p -> !p.archived);
    }

    @Test
    void testListArchivedProjects() {
        List<Project> projects = dayCaptain.getProjects(true);
        assertThat(projects.size()).isGreaterThanOrEqualTo(2);
        assertThat(projects).isSortedAccordingTo(Comparator.comparing(project -> project.string));
        assertThat(projects).allMatch(p -> p.archived);
    }

}
