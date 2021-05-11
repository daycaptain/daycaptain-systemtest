package com.daycaptain.systemtest.frontend.weeks.create;

import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.scenarios.CreateListItemScenario;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateWeekTaskUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);

    private TaskList tasks;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new CreateListItemScenario("week_create_week_task", this::beforeEach, () -> tasks).testCases();
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
        system.deleteWeekTasks(week);
        tasks = dayCaptain.week(week).weekTasks();
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
