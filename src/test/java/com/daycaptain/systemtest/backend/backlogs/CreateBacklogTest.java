package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateBacklogTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteBacklogs("New backlog");
    }

    @Test
    void testCreateBacklogItem() {
        URI itemId = dayCaptain.createBacklog("New backlog");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog._self).isEqualTo(itemId);
        assertThat(backlog.name).isEqualTo("New backlog");
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isNull();
        assertThat(backlog.project).isNull();
    }

    @Test
    void testCreateBacklogWithArea() {
        URI itemId = dayCaptain.createBacklogWithArea("New backlog", "IT work");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog._self).isEqualTo(itemId);
        assertThat(backlog.name).isEqualTo("New backlog");
        assertThat(backlog.area).isEqualTo("IT work");
        assertThat(backlog.relatedArea).isEqualTo("IT work");
        assertThat(backlog.project).isNull();
    }

    @Test
    void testCreateBacklogWithProject() {
        URI itemId = dayCaptain.createBacklogWithProject("New backlog", "Business idea");
        assertThat(itemId).isNotNull();

        Backlog backlog = dayCaptain.getBacklog(itemId);
        assertThat(backlog._self).isEqualTo(itemId);
        assertThat(backlog.name).isEqualTo("New backlog");
        assertThat(backlog.area).isNull();
        assertThat(backlog.relatedArea).isEqualTo("Business");
        assertThat(backlog.project).isEqualTo("Business idea");
    }

}
