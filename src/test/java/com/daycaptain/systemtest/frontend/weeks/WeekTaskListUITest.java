package com.daycaptain.systemtest.frontend.weeks;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import com.daycaptain.systemtest.frontend.entity.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import static org.assertj.core.api.Assertions.assertThat;

public class WeekTaskListUITest {

    private static final DayCaptainUI dayCaptain = new DayCaptainUI();
    private static final DayCaptainSystem system = new DayCaptainSystem();
    private static final YearWeek week = YearWeek.of(2021, 7);

    private TaskList weekTasks;

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
        system.deleteWeekTasks(week);
        weekTasks = dayCaptain.week(week).weekTasks();
    }

    @Test
    void moveTask() {
        weekTasks.createSave("Task 1");
        weekTasks.createSave("Task 2");
        weekTasks.createSave("Task 3");
        weekTasks.createSave("Task 4");
        weekTasks.move(3, -2);
        weekTasks.move(0, 2);
        assertThat(weekTasks.getNames()).containsExactly("Task 4", "Task 2", "Task 1", "Task 3");
    }

    @Test
    void moveTaskWithJump() {
        weekTasks.createSave("Task 1");
        weekTasks.createSave("Task 2");
        weekTasks.createSave("Task 3");
        weekTasks.createSave("Task 4");
        weekTasks.move(3, -2, true);
        weekTasks.move(0, 2, true);
        assertThat(weekTasks.getNames()).containsExactly("Task 4", "Task 2", "Task 1", "Task 3");
    }

    @Test
    void plannedTime() {
        weekTasks.createSave("Task 1");
        weekTasks.editPlannedTime(0, 8);
        weekTasks.editPlannedTime(0, -2);
        assertThat(weekTasks.getList().get(0).plannedTime).isEqualTo(3.0);
    }

    // is this expected behaviour? the jump version adds a whole hour
    @Test
    void plannedTimeWithJump() {
        weekTasks.createSave("Task 1");
        weekTasks.editPlannedTime(0, 8, true);
        weekTasks.editPlannedTime(0, -2, true);
        assertThat(weekTasks.getList().get(0).plannedTime).isEqualTo(6.0);
    }

    @Test
    void finishTask() {
        weekTasks.createSave("Task 1");
        weekTasks.finish(0);
        Task task = weekTasks.getList().get(0);
        assertThat(task.string).isEqualTo("Task 1");
        assertThat(task.status).isEqualTo("DONE");
    }

    @Test
    void cancelTask() {
        weekTasks.createSave("Task 1");
        weekTasks.cancel(0);
        assertThat(weekTasks.getList().get(0).status).isEqualTo("CANCELLED");
    }

    @Test
    void undoTaskStatus() {
        weekTasks.createSave("Task 1");
        weekTasks.cancel(0);
        weekTasks.undoStatus(0);
        assertThat(weekTasks.getList().get(0).status).isEqualTo("OPEN");
    }

}
