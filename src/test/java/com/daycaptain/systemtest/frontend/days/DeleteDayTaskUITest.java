package com.daycaptain.systemtest.frontend.days;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteDayTaskUITest {

    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private TaskList tasks;

    @BeforeEach
    void beforeEach() {
        system.deleteDayTasks(date);

        tasks = dayCaptain.day(date).tasks();
        tasks.createSave("New task");
        tasks.createSave("Another task");
        tasks.createSave("Third task");
    }

    @Test
    void delete_in_task_list() {
        tasks.delete(1);

        assertThat(tasks.getNames()).containsExactly("New task", "Third task");
    }

    @Test
    void delete_in_task_list_click() {
        tasks.clickDelete(1);

        assertThat(tasks.getNames()).containsExactly("New task", "Third task");
    }

    @Test
    void delete_in_edit_action_click() {
        EditTaskAction edit = tasks.edit(1);

        edit.clickDelete();
        assertThat(tasks.getNames()).containsExactly("New task", "Third task");
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
