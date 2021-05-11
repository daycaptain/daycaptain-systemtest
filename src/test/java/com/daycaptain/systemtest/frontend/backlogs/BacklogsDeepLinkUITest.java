package com.daycaptain.systemtest.frontend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditBacklogAction;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.views.BacklogsView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.daycaptain.systemtest.backend.CollectionUtils.findBacklog;
import static org.assertj.core.api.Assertions.assertThat;

public class BacklogsDeepLinkUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();

    @Test
    void inbox_item_deep_link_selected() {
        URI uri = system.createInboxItem("New backlog item");

        BacklogsView backlogs = dayCaptain.backlogs();
        backlogs.selectBacklog("To-contact");

        BacklogsView backlogsLink = dayCaptain.backlogsLink(uri);
        EditBacklogItemAction edit = backlogsLink.focusedBacklogItemList().edit();
        assertThat(edit.getName()).isEqualTo("New backlog item");
    }

    @Test
    void backlog_item_deep_link_selected() {
        Backlog backlog = findBacklog(system.getBacklogs(), "To-contact");
        URI uri = system.createBacklogItem("New contact item", backlog);

        BacklogsView backlogs = dayCaptain.backlogs();
        backlogs.selectInbox();

        BacklogsView backlogsLink = dayCaptain.backlogsLink(uri);
        EditBacklogItemAction edit = backlogsLink.focusedBacklogItemList().edit();
        assertThat(edit.getName()).isEqualTo("New contact item");
    }

    @Test
    void inbox_deep_link_selected() {
        URI uri = system.getInbox()._self;

        BacklogsView backlogs = dayCaptain.backlogs();
        backlogs.selectBacklog("To-contact");

        BacklogsView backlogsLink = dayCaptain.backlogsLink(uri);
        ListItem inbox = backlogsLink.focusedBacklogList().focusedItem();
        assertThat(inbox.string).isEqualTo("INBOX");
    }

    @Test
    void backlog_deep_link_selected() {
        URI uri = findBacklog(system.getBacklogs(), "To-contact")._self;

        BacklogsView backlogs = dayCaptain.backlogs();
        backlogs.selectInbox();

        BacklogsView backlogsLink = dayCaptain.backlogsLink(uri);
        EditBacklogAction edit = backlogsLink.focusedBacklogList().edit();
        assertThat(edit.getName()).isEqualTo("To-contact");
    }

    @BeforeEach
    void beforeEach() {
        system.deleteBacklogItemsInAllBacklogs("New contact item");
    }

    @BeforeAll
    public static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    public static void afterAll() {
        dayCaptain.close();
    }

}
