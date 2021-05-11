package com.daycaptain.systemtest.frontend.weeks.create;

import com.daycaptain.systemtest.frontend.scenarios.CreateListItemScenario;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTaskUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final DayOfWeek dayOfWeek = date.getDayOfWeek();

    private TaskList tasks;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new CreateListItemScenario("week_create_day_task", this::beforeEach, () -> tasks).testCases();
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
        assertThat(item.hasNote).isFalse();

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
        tasks = dayCaptain.week(week).dayTasks(dayOfWeek);
    }

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

//    @Test
//    void fromWeekTask() {
//        weekView.weekTasks().create("Working on my project");
//        assertThat(weekView.weekTasks().getNames()).containsExactly("Working on my project");
//
//        weekView.assignDayTaskFromWeekTask(0, "New task, assigned from week task", date.getDayOfWeek());
//        assertThat(weekView.dayTasks(date.getDayOfWeek()).getNames()).containsExactly("New task, assigned from week task");
//
//        date = date.minusDays(3);
//        weekView.assignDayTaskFromWeekTask(0, "New task, assigned from week task", date.getDayOfWeek());
//        assertThat(weekView.dayTasks(date.getDayOfWeek()).getNames()).containsExactly("New task, assigned from week task");
//    }

}
