package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklogItem;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateBacklogItemTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testCreateInboxItem() {
        URI itemId = dayCaptain.createInboxItem("Backlog item");
        assertThat(itemId).isNotNull();

        BacklogItem backlogItem = dayCaptain.getBacklogItem(itemId);
        assertThat(backlogItem._self).isEqualTo(itemId);
        assertThat(backlogItem.string).isEqualTo("Backlog item");
        assertThat(backlogItem.note).isNull();
        assertThat(backlogItem.status).isEqualTo("OPEN");
        assertThat(backlogItem.area).isNull();
        assertThat(backlogItem.relatedArea).isNull();
        assertThat(backlogItem.project).isNull();
        assertThat(backlogItem.assignedTasks).isEmpty();
    }

    @Test
    void testCreateInboxItemWithNote() {
        URI itemId = dayCaptain.createInboxItemWithNote("Backlog item", "A note");
        assertThat(itemId).isNotNull();

        BacklogItem backlogItem = dayCaptain.getBacklogItem(itemId);
        assertThat(backlogItem._self).isEqualTo(itemId);
        assertThat(backlogItem.string).isEqualTo("Backlog item");
        assertThat(backlogItem.note).isEqualTo("A note");
        assertThat(backlogItem.status).isEqualTo("OPEN");
        assertThat(backlogItem.area).isNull();
        assertThat(backlogItem.relatedArea).isNull();
        assertThat(backlogItem.project).isNull();
        assertThat(backlogItem.assignedTasks).isEmpty();
    }

    @Test
    void testCreateBacklogItem() {
        Backlog backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "To-contact");
        URI itemId = dayCaptain.createBacklogItem("Backlog item", backlog);
        assertThat(itemId).isNotNull();

        BacklogItem backlogItem = findBacklogItem(dayCaptain.getBacklog(backlog._self).items, itemId);
        assertThat(backlogItem._self).isEqualTo(itemId);
        assertThat(backlogItem.string).isEqualTo("Backlog item");
        assertThat(backlogItem.note).isNull();
        assertThat(backlogItem.status).isEqualTo("OPEN");
        assertThat(backlogItem.area).isNull();
        assertThat(backlogItem.relatedArea).isNull();
        assertThat(backlogItem.project).isNull();
        assertThat(backlogItem.assignedTasks).isEmpty();
    }

    @Test
    void testCreateBacklogItemWithNote() {
        Backlog backlog = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "To-contact");
        URI itemId = dayCaptain.createBacklogItemWithNote("Backlog item", "A note", backlog);
        assertThat(itemId).isNotNull();

        BacklogItem backlogItem = findBacklogItem(dayCaptain.getBacklog(backlog._self).items, itemId);
        assertThat(backlogItem._self).isEqualTo(itemId);
        assertThat(backlogItem.string).isEqualTo("Backlog item");
        assertThat(backlogItem.note).isEqualTo("A note");
        assertThat(backlogItem.status).isEqualTo("OPEN");
        assertThat(backlogItem.area).isNull();
        assertThat(backlogItem.relatedArea).isNull();
        assertThat(backlogItem.project).isNull();
        assertThat(backlogItem.assignedTasks).isEmpty();
    }

    // TODO missing

}
