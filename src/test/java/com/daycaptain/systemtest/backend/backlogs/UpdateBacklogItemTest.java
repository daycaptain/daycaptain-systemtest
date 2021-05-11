package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateBacklogItemTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUpdateName() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("New backlog item");

        dayCaptain.updateBacklogItem(item, "string", "Very new backlog item");

        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.string).isEqualTo("Very new backlog item");
    }

    @Test
    void testUpdateArea() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.area).isNull();

        dayCaptain.updateBacklogItem(item, "area", "IT work");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.area).isEqualTo("IT work");

        dayCaptain.updateBacklogItem(item, "area", "Business");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.area).isEqualTo("Business");
    }

    @Test
    void testRemoveArea() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        dayCaptain.updateBacklogItem(item, "area", "IT work");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.area).isEqualTo("IT work");

        dayCaptain.updateBacklogItem(item, "area", null);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.area).isNull();
    }

    @Test
    void testUpdateProject() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.project).isNull();

        dayCaptain.updateBacklogItem(item, "project", "Business idea");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.project).isEqualTo("Business idea");
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isEqualTo("Business");

        dayCaptain.updateBacklogItem(item, "project", "Work presentations");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.project).isEqualTo("Work presentations");
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isEqualTo("IT work");
    }

    @Test
    void testRemoveProject() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem item = dayCaptain.getBacklogItem(itemId);

        dayCaptain.updateBacklogItem(item, "project", "Business idea");
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.project).isEqualTo("Business idea");
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isEqualTo("Business");

        dayCaptain.updateBacklogItem(item, "project", null);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.project).isNull();
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isNull();
    }

    @Test
    void testMoveBacklogItem() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        assertThat(itemId).isNotNull();

        Backlog backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX");
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.backlog).isEqualTo(backlog._self);
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isNull();
        assertThat(item.project).isNull();
        assertThat(item.relatedProject).isNull();

        backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "IT work");
        dayCaptain.updateBacklogItem(item, "backlog", backlog._self);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.backlog).isEqualTo(backlog._self);
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isEqualTo("IT work");
        assertThat(item.project).isNull();
        assertThat(item.relatedProject).isNull();

        backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "Business idea");
        dayCaptain.updateBacklogItem(item, "backlog", backlog._self);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.backlog).isEqualTo(backlog._self);
        assertThat(item.area).isNull();
        assertThat(item.relatedArea).isEqualTo("Business");
        assertThat(item.project).isNull();
        assertThat(item.relatedProject).isEqualTo("Business idea");
    }

    @Test
    void testArchiveAndRestoreDone() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isFalse();
        dayCaptain.updateBacklogItem(item, "status", "DONE");

        dayCaptain.updateBacklogItem(item, "archived", true);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isTrue();

        dayCaptain.updateBacklogItem(item, "archived", false);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isFalse();
    }

    @Test
    void testArchiveAndRestoreCanceled() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isFalse();
        dayCaptain.updateBacklogItem(item, "status", "CANCELLED");

        dayCaptain.updateBacklogItem(item, "archived", true);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isTrue();

        dayCaptain.updateBacklogItem(item, "archived", false);
        item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isFalse();
    }

    @Test
    void testOpenBacklogItemCantBeArchived() {
        URI itemId = dayCaptain.createInboxItem("New backlog item");
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        assertThat(item.archived).isFalse();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateBacklogItem(item, "archived", true));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

}
