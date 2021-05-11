package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.scenarios.CreateListItemScenario;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTaskUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private TaskList tasks;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new CreateListItemScenario("create_day_task", this::beforeEach, () -> tasks).testCases();
    }

    @Test
    void withPlannedTime() {
        CreateTaskAction createAction = tasks.create();
        createAction.setName("New task");
        createAction.setPlannedTime(4, false);
        createAction.save();

        EditTaskAction editTaskAction = tasks.edit(0);
        assertThat(editTaskAction.getPlannedTime()).isEqualTo("2.00");
        assertThat(editTaskAction.getProject()).isEqualTo("No project");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        editTaskAction.close();
    }

    @Test
    void withNote() {
        CreateAction createAction = tasks.create();
        createAction.setName("New item");
        createAction.setNote("This is a note.");
        createAction.save();

        ListItem item = tasks.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isTrue();

        EditInformationAction editTaskAction = tasks.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        assertThat(editTaskAction.getProject()).isEqualTo("No project");
        assertThat(editTaskAction.getNote()).isEqualTo("This is a note.");
        editTaskAction.close();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTasks(date);
        tasks = dayCaptain.day(date).tasks();
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
