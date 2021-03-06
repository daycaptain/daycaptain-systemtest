package com.daycaptain.systemtest.frontend.weeks.create;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.scenarios.CreateListItemScenario;
import com.daycaptain.systemtest.frontend.views.WeekView;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTimeEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final DayOfWeek dayOfWeek = date.getDayOfWeek();

    private DayTimeEventList events;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new CreateListItemScenario("week_create_day_time_event", this::beforeEach, () -> events).testCases();
    }

    @Test
    void withStartEndTime() {
        CreateDayTimeEventAction createAction = events.create();
        createAction.setName("New task");
        createAction.setStartTime("14:00");
        createAction.setEndTime("18:00");
        createAction.save();

        EditTimeEventAction editAction = events.edit(0);
        assertThat(editAction.getName()).isEqualTo("New task");
        assertThat(editAction.getStartTime()).isEqualTo("14:00");
        assertThat(editAction.getEndTime()).isEqualTo("18:00");
        assertThat(editAction.getProject()).isEqualTo("No project");
        assertThat(editAction.getArea()).isEqualTo("No area");
        editAction.close();
    }

    @Test
    void withNote() {
        CreateAction createAction = events.create();
        createAction.setName("New item");
        createAction.setNote("This is a note.");
        createAction.save();

        ListItem item = events.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isTrue();

        EditInformationAction editTaskAction = events.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        assertThat(editTaskAction.getProject()).isEqualTo("No project");
        assertThat(editTaskAction.getNote()).isEqualTo("This is a note.");
        editTaskAction.close();
    }

    @Test
    void withDetectedTime() {
        CreateDayTimeEventAction createAction = events.create();
        createAction.setName("Project");
        createAction.assertStartTime("08:00");
        createAction.assertEndTime("11:00");

        createAction.setName("Reading");
        createAction.assertStartTime("15:00");
        createAction.assertEndTime("16:15");

        createAction.setStartTime("13:00");
        createAction.setEndTime("14:00");

        createAction.setName("Project");
        createAction.assertStartTime("13:00");
        createAction.assertEndTime("14:00");
        createAction.close();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTimeEvents(date);
        WeekView week = dayCaptain.week(CreateDayTimeEventUITest.week);
        events = week.dayTimeEvents(dayOfWeek);
        assertThat(week.selectedDay()).contains("16th Feb");
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
