package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.entity.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class DayTaskListUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final YearWeek week = YearWeek.of(2021, 7);
    private static final LocalDate date = LocalDate.of(2021, 2, 16);
    private static final DayOfWeek dayOfWeek = date.getDayOfWeek();

    private TaskList dayTasks;

    @BeforeAll
    static void beforeAll() {
        dayCaptain.initWithLogin();
    }

    @AfterAll
    static void afterAll() {
        dayCaptain.close();
    }

    @BeforeEach
    void setUp() {
        system.deleteDayTasks(date);
        dayTasks = dayCaptain.week(week).dayTasks(dayOfWeek);
    }

    @Test
    void moveTask() {
        dayTasks.createSave("Task 1");
        dayTasks.createSave("Task 2");
        dayTasks.createSave("Task 3");
        dayTasks.createSave("Task 4");
        dayTasks.move(3, -2);
        dayTasks.move(0, 2);
        assertThat(dayTasks.getNames()).containsExactly("Task 4", "Task 2", "Task 1", "Task 3");
    }

    @Test
    void moveTaskWithJump() {
        dayTasks.createSave("Task 1");
        dayTasks.createSave("Task 2");
        dayTasks.createSave("Task 3");
        dayTasks.createSave("Task 4");
        dayTasks.move(3, -2, true);
        dayTasks.move(0, 2, true);
        assertThat(dayTasks.getNames()).containsExactly("Task 4", "Task 2", "Task 1", "Task 3");
    }

    @Test
    void assignPlannedTime() {
        dayTasks.createSave("Task 1");
        dayTasks.editPlannedTime(0, 8);
        dayTasks.editPlannedTime(0, -2);
        EditTaskAction editTaskAction = dayTasks.edit(0);
        assertThat(editTaskAction.getPlannedTime()).isEqualTo("3.00");
        editTaskAction.close();
    }

    // is this expected behaviour? the jump version adds a whole hour
    @Test
    void assignPlannedTimeWithJump() {
        dayTasks.createSave("Task 1");
        dayTasks.editPlannedTime(0, 8, true);
        dayTasks.editPlannedTime(0, -2, true);
        EditTaskAction editTaskAction = dayTasks.edit(0);
        assertThat(editTaskAction.getPlannedTime()).isEqualTo("6.00");
        editTaskAction.close();
    }

    @Test
    void finishTask() {
        dayTasks.createSave("Task 1");
        dayTasks.finish(0);
        Task task = dayTasks.getList().get(0);
        assertThat(task.string).isEqualTo("Task 1");
        assertThat(task.status).isEqualTo("DONE");
    }

    @Test
    void cancelTask() {
        dayTasks.createSave("Task 1");
        dayTasks.cancel(0);
        assertThat(dayTasks.getList().get(0).status).isEqualTo("CANCELLED");
    }

    @Test
    void undoTaskStatus() {
        dayTasks.createSave("Task 1");
        dayTasks.cancel(0);
        dayTasks.undoStatus(0);
        assertThat(dayTasks.getList().get(0).status).isEqualTo("OPEN");
    }

}
