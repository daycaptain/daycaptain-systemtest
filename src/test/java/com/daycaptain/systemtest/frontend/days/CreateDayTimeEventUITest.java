package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.entity.TimeEvent;
import com.daycaptain.systemtest.frontend.scenarios.CreateListItemScenario;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Collection;

import static com.daycaptain.systemtest.Times.berlin;
import static com.daycaptain.systemtest.Times.moscow;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateDayTimeEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);

    private DayTimeEventList events;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new CreateListItemScenario("create_day_time_event", this::beforeEach, () -> events).testCases();
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

        TimeEvent item = events.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isTrue();
        assertThat(item.timeZoneSwitch).isFalse();

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

    @Test
    void withTimeZoneSwitch() {
        CreateDayTimeEventAction createAction = events.create();
        createAction.setName("New item");
        createAction.setStartTime("10:00");
        createAction.setStartTimeZone(berlin);
        createAction.setEndTime("11:00");
        createAction.setEndTimeZone(moscow);

        createAction.assertStartTime("10:00");
        createAction.assertEndTime("13:00");

        createAction.save();

        TimeEvent item = events.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();
        assertThat(item.timeZoneSwitch).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTimeEvents(date);
        DayView dayView = dayCaptain.day(date);
        events = dayView.timeEvents();
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
