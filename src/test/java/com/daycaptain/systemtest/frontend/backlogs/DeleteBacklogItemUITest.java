package com.daycaptain.systemtest.frontend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.elements.BacklogItemList;
import com.daycaptain.systemtest.frontend.views.BacklogsView;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteBacklogItemUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private BacklogItemList itemList;

    @BeforeEach
    void beforeEach() {
        cleanUp();
        system.createBacklog("DeleteBacklogItemUITest");

        BacklogsView backlogs = dayCaptain.backlogs();
        backlogs.backlogList().selectLast();
        itemList = backlogs.backlogItemList();
        itemList.createSave("Task 1");
        itemList.createSave("Task 2");
        itemList.createSave("Task 3");
    }

    @AfterEach
    void cleanUp() {
        system.deleteBacklogs("DeleteBacklogItemUITest");
    }

    @Test
    void delete_in_task_list() {
        itemList.delete(1);

        assertThat(itemList.getNames()).containsSequence("Task 1", "Task 3");
    }

    @Test
    void delete_in_task_list_click() {
        itemList.clickDelete(1);

        assertThat(itemList.getNames()).containsSequence("Task 1", "Task 3");
    }

    @Test
    void delete_in_edit_action_click() {
        EditBacklogItemAction edit = itemList.edit(1);

        edit.clickDelete();
        assertThat(itemList.getNames()).containsSequence("Task 1", "Task 3");
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
