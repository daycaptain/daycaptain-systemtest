package com.daycaptain.systemtest.backend.projects;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Project;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ListProjectsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testListActiveProjects() {
        List<Project> projects = dayCaptain.getProjects(false);
        assertThat(projects.size()).isGreaterThanOrEqualTo(3);
        assertThat(projects).isSortedAccordingTo(Comparator.comparing(project -> project.string));
        assertThat(projects).allMatch(p -> !p.archived);
        assertThat(projects).extracting("string").contains("Business idea", "Spanish", "Work presentations");
    }

    @Test
    void testListArchivedProjects() {
        List<Project> projects = dayCaptain.getProjects(true);
        assertThat(projects.size()).isGreaterThanOrEqualTo(5);
        assertThat(projects).isSortedAccordingTo(Comparator.comparing((Project project) -> project.archived).thenComparing(project -> project.string));
        assertThat(projects).anyMatch(p -> p.archived);
        assertThat(projects).extracting("string").contains("Business idea", "Spanish", "Work presentations", "Past business idea", "Past presentations");
    }

}
