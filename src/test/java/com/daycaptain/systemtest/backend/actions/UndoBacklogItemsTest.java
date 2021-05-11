package com.daycaptain.systemtest.backend.actions;

import com.daycaptain.systemtest.backend.CollectionUtils;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklogItem;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class UndoBacklogItemsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testUndoBacklogItemDeletion() {
        URI taskId = dayCaptain.createInboxItem("New item");
        URI inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX")._self;
        assertThat(taskId).isNotNull();

        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New item");

        String actionId = dayCaptain.deleteBacklogItem(item);
        item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item).isNull();

        dayCaptain.undo(actionId);

        item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New item");
    }

    @Test
    void testUndoBacklogItemUpdate() {
        URI taskId = dayCaptain.createInboxItem("New item");
        URI inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX")._self;
        assertThat(taskId).isNotNull();

        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New item");

        String actionId = dayCaptain.updateBacklogItem(item, "string", "New task");

        item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New task");

        dayCaptain.undo(actionId);

        item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New item");
    }

    @Test
    void testUndoBacklogItemResort() {
        URI inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX")._self;
        dayCaptain.getBacklog(inbox).items.forEach(dayCaptain::deleteBacklogItem);

        URI taskId = dayCaptain.createInboxItem("First");
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createInboxItem("Second");
        assertThat(taskId).isNotNull();

        taskId = dayCaptain.createInboxItem("Third");
        assertThat(taskId).isNotNull();

        List<BacklogItem> items = dayCaptain.getBacklog(inbox).items;
        assertThat(items).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        String actionId = dayCaptain.updateBacklogItem(items.get(1), "priority", 1);

        items = dayCaptain.getBacklog(inbox).items;
        assertThat(items).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        dayCaptain.undo(actionId);

        items = dayCaptain.getBacklog(inbox).items;
        assertThat(items).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @Test
    void testUndoMoveBacklogItem() {
        URI inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX")._self;
        URI backlog = dayCaptain.createBacklog("Backlog");
        URI taskId = dayCaptain.createInboxItem("New item");
        assertThat(taskId).isNotNull();

        BacklogItem item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item.string).isEqualTo("New item");

        String actionId = dayCaptain.updateBacklogItem(item, "backlog", backlog);

        item = findBacklogItem(dayCaptain.getBacklog(inbox).items, taskId);
        assertThat(item).isNull();
        List<BacklogItem> items = dayCaptain.getBacklog(backlog).items;
        Assertions.assertThat(CollectionUtils.findBacklogItem(items, taskId).string).isEqualTo("New item");

        dayCaptain.undo(actionId);

        items = dayCaptain.getBacklog(inbox).items;
        Assertions.assertThat(CollectionUtils.findBacklogItem(items, taskId).string).isEqualTo("New item");
        items = dayCaptain.getBacklog(backlog).items;
        Assertions.assertThat(CollectionUtils.findBacklogItem(items, taskId)).isNull();
    }

    @AfterEach
    void tearDown() {
        URI inbox = CollectionUtils.findBacklog(dayCaptain.getBacklogs(), "INBOX")._self;
        dayCaptain.getBacklog(inbox).items.forEach(dayCaptain::deleteBacklogItem);
        dayCaptain.deleteBacklogs("Backlog");
    }

}
