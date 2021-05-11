package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateBacklogTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteBacklogs("New backlog");
        dayCaptain.deleteBacklogs("Very new backlog");
    }

    @Test
    void testUpdateName() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.name).isEqualTo("New backlog");

        dayCaptain.updateBacklog(backlog, "name", "Very new backlog");

        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.name).isEqualTo("Very new backlog");
    }

    @Test
    void testUpdateArea() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.area).isNull();

        dayCaptain.updateBacklog(backlog, "area", "IT work");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.area).isEqualTo("IT work");

        dayCaptain.updateBacklog(backlog, "area", "Business");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.area).isEqualTo("Business");
    }

    @Test
    void testRemoveArea() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);

        dayCaptain.updateBacklog(backlog, "area", "IT work");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.area).isEqualTo("IT work");

        dayCaptain.updateBacklog(backlog, "area", null);
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.area).isNull();
    }

    @Test
    void testUpdateProject() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.project).isNull();

        dayCaptain.updateBacklog(backlog, "project", "Business idea");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.project).isEqualTo("Business idea");
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isEqualTo("Business");

        dayCaptain.updateBacklog(backlog, "project", "Work presentations");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.project).isEqualTo("Work presentations");
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isEqualTo("IT work");
    }

    @Test
    void testRemoveProject() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);

        dayCaptain.updateBacklog(backlog, "project", "Business idea");
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.project).isEqualTo("Business idea");
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isEqualTo("Business");

        dayCaptain.updateBacklog(backlog, "project", null);
        backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog.project).isNull();
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isNull();
    }

}
