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

public class UpdateDayTimeEventUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 3, 16);

    private DayTimeEventList events;

    @BeforeEach
    void beforeEach() {
        system.deleteDayTimeEvents(date);
        DayView dayView = dayCaptain.day(date);
        events = dayView.timeEvents();
    }

    @Test
    void update_project_resets_area() {
        CreateDayTimeEventAction create = events.create();
        create.setName("New event");
        create.setStartTime("10:00");
        create.setEndTime("12:00");
        create.setArea("i");
        create.save();

        EditTimeEventAction edit = events.edit();
        edit.setProject("Business idea");
        edit.save();

        edit = events.edit();
        assertThat(edit.getProject()).isEqualTo("Business idea");
        assertThat(edit.getArea()).isEqualTo("Business");
        edit.close();
    }

    @Test
    void update_area_resets_project() {
        CreateDayTimeEventAction create = events.create();
        create.setName("New event");
        create.setStartTime("10:00");
        create.setEndTime("12:00");
        create.setProject("Business idea");
        create.save();

        EditTimeEventAction edit = events.edit();
        edit.setArea("i");
        edit.save();

        edit = events.edit();
        assertThat(edit.getProject()).isEqualTo("No project");
        assertThat(edit.getArea()).isEqualTo("IT work");
        edit.close();
    }

    @Test
    void update_project_mouse() {
        CreateDayTimeEventAction create = events.create();
        create.setName("New event");
        create.setStartTime("10:00");
        create.setEndTime("12:00");
        create.setAreaClick("IT work");
        create.save();

        EditTimeEventAction edit = events.edit();
        edit.setProjectClick("Business idea");
        edit.save();

        edit = events.edit();
        assertThat(edit.getProject()).isEqualTo("Business idea");
        assertThat(edit.getArea()).isEqualTo("Business");
        edit.close();
    }

    @Test
    void update_area_mouse() {
        CreateDayTimeEventAction create = events.create();
        create.setName("New event");
        create.setStartTime("10:00");
        create.setEndTime("12:00");
        create.setProjectClick("Business idea");
        create.save();

        EditTimeEventAction edit = events.edit();
        edit.setAreaClick("IT work");
        edit.save();

        edit = events.edit();
        assertThat(edit.getProject()).isEqualTo("No project");
        assertThat(edit.getArea()).isEqualTo("IT work");
        edit.close();
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
