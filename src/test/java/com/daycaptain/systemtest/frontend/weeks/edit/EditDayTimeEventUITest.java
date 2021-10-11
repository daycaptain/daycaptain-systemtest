package com.daycaptain.systemtest.frontend.weeks.edit;

import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.scenarios.EditListItemScenario;
import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class EditDayTimeEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final YearWeek week = YearWeek.of(2021, 7);
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final DayOfWeek dayOfWeek = date.getDayOfWeek();

    private DayTimeEventList events;

    @TestFactory
    Collection<DynamicTest> testCases() {
        return new EditListItemScenario("week_create_day_time_event", this::beforeEach, () -> events).testCases();
    }

    @Test
    void startEndTime() {
        events.createSave("New task");
        EditTimeEventAction editTimeEventAction = events.edit(0);
        editTimeEventAction.setStartTime("14:00");
        editTimeEventAction.setEndTime("16:00");
        editTimeEventAction.save();

        Assertions.assertThat(events.getList().get(0).string).isEqualTo("New task");
        Assertions.assertThat(events.getList().get(0).project).isNull();
        Assertions.assertThat(events.getList().get(0).hasArea).isFalse();

        EditTimeEventAction retrieveAction = events.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getStartTime()).isEqualTo("14:00");
        assertThat(retrieveAction.getEndTime()).isEqualTo("16:00");
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    @BeforeEach
    void beforeEach() {
        system.deleteDayTimeEvents(date);
        events = dayCaptain.week(week).dayTimeEvents(dayOfWeek);
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
