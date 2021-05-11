package com.daycaptain.systemtest.frontend.weeks.edit;

import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.scenarios.EditListItemScenario;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class EditWeekTaskUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);

    private TaskList tasks;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new EditListItemScenario("week_edit_week_task", this::beforeEach, () -> tasks).testCases();
    }

    @Test
    void plannedTime() {
        tasks.create("New task");
        EditTaskAction editTaskAction = tasks.edit(0);
        editTaskAction.setPlannedTime(6, false);
        editTaskAction.setPlannedTime(-2, true);
        editTaskAction.save();

        Assertions.assertThat(tasks.getList().get(0).string).isEqualTo("New task");
        Assertions.assertThat(tasks.getList().get(0).project).isNull();
        Assertions.assertThat(tasks.getList().get(0).hasArea).isFalse();

        EditTaskAction retrieveAction = tasks.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getPlannedTime()).isEqualTo("1.00");
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteWeekTasks(week);
        tasks = dayCaptain.week(week).weekTasks();
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

}
