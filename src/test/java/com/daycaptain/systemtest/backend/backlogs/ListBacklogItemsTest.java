package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.backend.entity.BacklogItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListBacklogItemsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteBacklogs("Test");
        dayCaptain.getInbox(true).items.forEach(dayCaptain::deleteBacklogItem);
    }

    @Test
    void testListBacklogItems() {
        Backlog backlog = dayCaptain.getBacklog(dayCaptain.createBacklog("Test"));

        dayCaptain.createBacklogItem("First", backlog);
        dayCaptain.createBacklogItem("Second", backlog);
        dayCaptain.createBacklogItem("Third", backlog);

        List<BacklogItem> items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(b -> b.string).containsExactly("First", "Second", "Third");
    }

    @Test
    void testListInboxItemsFilterArea() {
        dayCaptain.createInboxItem("First");
        dayCaptain.createInboxItemWithArea("Second", "IT work");
        dayCaptain.createInboxItem("Third");

        List<BacklogItem> items = dayCaptain.getInbox().items;
        assertThat(items).extracting(b -> b.string).containsSequence("First", "Second", "Third");

        items = dayCaptain.getInboxFilterArea("IT work").items;
        assertThat(items).extracting(b -> b.string).contains("Second");
        assertThat(items).extracting(b -> b.string).doesNotContain("First", "Third");
    }

    @Test
    void testListInboxItemsFilterProject() {
        dayCaptain.createInboxItem("First");
        dayCaptain.createInboxItemWithProject("Second", "Business idea");
        dayCaptain.createInboxItem("Third");

        List<BacklogItem> items = dayCaptain.getInbox().items;
        assertThat(items).extracting(b -> b.string).containsSequence("First", "Second", "Third");

        items = dayCaptain.getInboxFilterProject("Business idea").items;
        assertThat(items).extracting(b -> b.string).contains("Second");
        assertThat(items).extracting(b -> b.string).doesNotContain("First", "Third");
    }

    @Test
    void testListBacklogItemsFilterArea() {
        Backlog backlog = dayCaptain.getBacklog(dayCaptain.createBacklog("Test"));

        dayCaptain.createBacklogItem("First", backlog);
        dayCaptain.createBacklogItemWithArea("Second", backlog, "IT work");
        dayCaptain.createBacklogItemWithArea("Third", backlog, "Business");

        List<BacklogItem> items = dayCaptain.getBacklogFilterArea(backlog._self, "IT work").items;
        assertThat(items).extracting(b -> b.string).containsExactly("Second");
        items = dayCaptain.getBacklogFilterArea(backlog._self, "Business").items;
        assertThat(items).extracting(b -> b.string).containsExactly("Third");
        items = dayCaptain.getBacklogFilterArea(backlog._self, "unknown").items;
        assertThat(items).extracting(b -> b.string).isEmpty();
    }

    @Test
    void testListBacklogItemsFilterProject() {
        Backlog backlog = dayCaptain.getBacklog(dayCaptain.createBacklog("Test"));

        dayCaptain.createBacklogItem("First", backlog);
        dayCaptain.createBacklogItemWithProject("Second", backlog, "Business idea");
        dayCaptain.createBacklogItemWithProject("Third", backlog, "Spanish");

        List<BacklogItem> items = dayCaptain.getBacklogFilterProject(backlog._self, "Business idea").items;
        assertThat(items).extracting(b -> b.string).containsExactly("Second");
        items = dayCaptain.getBacklogFilterProject(backlog._self, "Spanish").items;
        assertThat(items).extracting(b -> b.string).containsExactly("Third");
        items = dayCaptain.getBacklogFilterProject(backlog._self, "unknown").items;
        assertThat(items).extracting(b -> b.string).isEmpty();
    }

    @Test
    void testResort() {
        Backlog backlog = dayCaptain.getBacklog(dayCaptain.createBacklog("Test"));

        dayCaptain.createBacklogItem("First", backlog);
        dayCaptain.createBacklogItem("Second", backlog);
        dayCaptain.createBacklogItem("Third", backlog);

        List<BacklogItem> items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to top
        dayCaptain.updateBacklogItem(items.get(1), "priority", 1);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("Second", "First", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // middle to end
        dayCaptain.updateBacklogItem(items.get(1), "priority", 3);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to top
        dayCaptain.updateBacklogItem(items.get(2), "priority", 1);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("First", "Second", "Third");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // end to middle
        dayCaptain.updateBacklogItem(items.get(2), "priority", 2);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("First", "Third", "Second");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to end
        dayCaptain.updateBacklogItem(items.get(0), "priority", 3);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("Third", "Second", "First");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);

        // top to middle
        dayCaptain.updateBacklogItem(items.get(0), "priority", 2);
        items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(t -> t.string).containsExactly("Second", "Third", "First");
        assertThat(items).extracting(t -> t.priority).containsExactly(1, 2, 3);
    }

    @Test
    void testArchivedNotListedInInbox() {
        URI itemId = dayCaptain.createInboxItem("First");
        dayCaptain.createInboxItem("Second");
        dayCaptain.createInboxItem("Third");
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        dayCaptain.updateBacklogItem(item, "status", "DONE");
        dayCaptain.updateBacklogItem(item, "archived", true);

        List<BacklogItem> items = dayCaptain.getInbox().items;
        assertThat(items).extracting(b -> b.string).containsSequence("Second", "Third");

        items = dayCaptain.getInbox(true).items;
        assertThat(items).extracting(b -> b.string).containsSequence("Second", "Third", "First");
    }

    @Test
    void testArchivedNotListedInBacklog() {
        Backlog backlog = dayCaptain.getBacklog(dayCaptain.createBacklog("Test"));

        URI itemId = dayCaptain.createBacklogItem("First", backlog);
        dayCaptain.createBacklogItem("Second", backlog);
        dayCaptain.createBacklogItem("Third", backlog);
        BacklogItem item = dayCaptain.getBacklogItem(itemId);
        dayCaptain.updateBacklogItem(item, "status", "DONE");
        dayCaptain.updateBacklogItem(item, "archived", true);

        List<BacklogItem> items = dayCaptain.getBacklog(backlog._self).items;
        assertThat(items).extracting(b -> b.string).containsSequence("Second", "Third");

        items = dayCaptain.getBacklog(backlog._self, true).items;
        assertThat(items).extracting(b -> b.string).containsSequence("Second", "Third", "First");
    }

}
