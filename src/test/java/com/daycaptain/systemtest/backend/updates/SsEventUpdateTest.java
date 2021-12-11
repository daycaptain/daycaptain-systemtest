package com.daycaptain.systemtest.backend.updates;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.threeten.extra.YearWeek;

import java.net.URI;
import java.time.LocalDate;
import java.util.concurrent.locks.LockSupport;

import static com.daycaptain.systemtest.Times.time;
import static com.daycaptain.systemtest.backend.CollectionUtils.findArea;
import static com.daycaptain.systemtest.backend.CollectionUtils.findProject;
import static org.assertj.core.api.Assertions.assertThat;

class SsEventUpdateTest {

    private static final LocalDate date = LocalDate.of(2021, 12, 1);
    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();
    private int updateCount;

    @Test
    void should_send_updates_on_actions() {
        dayCaptain.registerCountUpdates();
        updateCount = dayCaptain.getRegisteredUpdates();
        URI dayTimeEvent = dayCaptain.createDayTimeEvent("Event", time(date, 11), time(date, 12));
        verifyUpdateIncrement();
        URI dayEvent = dayCaptain.createDayEvent("Event", date, date);
        verifyUpdateIncrement();
        URI dayTask = dayCaptain.createDayTask("Task", date);
        verifyUpdateIncrement();
        // should also count as single update
        URI dayTaskWithNote = dayCaptain.createDayTaskWithNote("Task", date, "A note");
        verifyUpdateIncrement();
        URI backlogUri = dayCaptain.createBacklog("SsEventUpdateTest backlog");
        verifyUpdateIncrement();
        Backlog backlog = dayCaptain.getBacklog(backlogUri);
        URI backlogItem = dayCaptain.createBacklogItem("SsEventUpdateTest backlog item", backlog);
        verifyUpdateIncrement();
        URI inboxItem = dayCaptain.createInboxItem("SsEventUpdateTest backlog item");
        verifyUpdateIncrement();
        URI weekTask = dayCaptain.createWeekTask("Task", YearWeek.from(date));
        verifyUpdateIncrement();
        URI area = dayCaptain.createArea("SsEventUpdateTest area");
        verifyUpdateIncrement();
        URI project = dayCaptain.createProject("SsEventUpdateTest project");
        verifyUpdateIncrement();

        dayCaptain.updateDayTimeEvent(dayCaptain.getDayTimeEvent(dayTimeEvent), "string", "updated");
        verifyUpdateIncrement();
        dayCaptain.updateDayEvent(dayCaptain.getDayEvent(dayEvent), "string", "updated");
        verifyUpdateIncrement();
        dayCaptain.updateTask(dayCaptain.getTask(dayTask), "string", "updated");
        verifyUpdateIncrement();
        dayCaptain.updateTask(dayCaptain.getTask(dayTaskWithNote), "note", "updated note");
        verifyUpdateIncrement();
        dayCaptain.updateBacklog(backlog, "name", "SsEventUpdateTest updated backlog");
        verifyUpdateIncrement();
        dayCaptain.updateBacklogItem(dayCaptain.getBacklogItem(backlogItem), "string", "SsEventUpdateTest backlog item");
        verifyUpdateIncrement();
        dayCaptain.updateTask(dayCaptain.getTask(weekTask), "string", "updated");
        verifyUpdateIncrement();
        dayCaptain.updateArea(dayCaptain.getArea(area), "keyCode", "X");
        verifyUpdateIncrement();
        dayCaptain.updateProject(dayCaptain.getProject(project), "string", "SsEventUpdateTest updated project");
        verifyUpdateIncrement();

        dayCaptain.addRelation(dayCaptain.getTask(dayTask), dayTimeEvent);
        verifyUpdateIncrement();
        dayCaptain.migrateProjectToArea(dayCaptain.getProject(project));
        verifyUpdateIncrement();
        dayCaptain.migrateAreaToProject(findArea(dayCaptain.getAreas(), "SsEventUpdateTest updated project"));
        verifyUpdateIncrement();

        dayCaptain.deleteDayTimeEvent(dayCaptain.getDayTimeEvent(dayTimeEvent));
        verifyUpdateIncrement();
        dayCaptain.deleteDayEvent(dayCaptain.getDayEvent(dayEvent));
        verifyUpdateIncrement();
        dayCaptain.deleteTask(dayCaptain.getTask(dayTask));
        verifyUpdateIncrement();
        dayCaptain.deleteBacklogItem(dayCaptain.getBacklogItem(backlogItem));
        verifyUpdateIncrement();
        dayCaptain.deleteBacklog(backlog);
        verifyUpdateIncrement();
        dayCaptain.deleteBacklogItem(dayCaptain.getBacklogItem(inboxItem));
        verifyUpdateIncrement();
        dayCaptain.deleteTask(dayCaptain.getTask(weekTask));
        verifyUpdateIncrement();
        dayCaptain.updateTask(dayCaptain.getTask(dayTaskWithNote), "note", null);
        verifyUpdateIncrement();
        dayCaptain.deleteArea(dayCaptain.getArea(area));
        verifyUpdateIncrement();
        dayCaptain.deleteProject(findProject(dayCaptain.getProjects(false), "SsEventUpdateTest updated project"));
        verifyUpdateIncrement();
    }

    @Test
    void should_send_updates_on_undo_actions() {
        URI task = dayCaptain.createDayTask("Task", date);
        String actionId = dayCaptain.updateTask(dayCaptain.getTask(task), "string", "updated");

        dayCaptain.registerCountUpdates();
        updateCount = dayCaptain.getRegisteredUpdates();
        dayCaptain.undo(actionId);
        verifyUpdateIncrement();
    }

    @Test
    void should_not_send_updates_on_search_or_detects() {
        URI task = dayCaptain.createDayTask("Task", date);

        dayCaptain.registerCountUpdates();
        dayCaptain.detect("Task");
        dayCaptain.detectWithTimes("Task");
        dayCaptain.search("Task");
        dayCaptain.searchPotentialRelations(task, "Task");

        LockSupport.parkNanos(1_000_000_000L);
        assertThat(dayCaptain.getRegisteredUpdates()).isZero();
    }

    @AfterEach
    void tearDown() {
        dayCaptain.deleteDayTimeEvents(date);
        dayCaptain.deleteDayEvents(date);
        dayCaptain.deleteDayTasks(date);
        dayCaptain.deleteWeekTasks(YearWeek.from(date));
        dayCaptain.deleteBacklogs("SsEventUpdateTest backlog");
        dayCaptain.deleteBacklogs("SsEventUpdateTest updated backlog");
        dayCaptain.deleteBacklogItemsInAllBacklogs("SsEventUpdateTest backlog item", "SsEventUpdateTest updated backlog item");
        dayCaptain.deleteAreas("SsEventUpdateTest area", "SsEventUpdateTest updated project");
        dayCaptain.deleteProjects("SsEventUpdateTest project", "SsEventUpdateTest updated project");

        dayCaptain.close();
    }

    private void verifyUpdateIncrement() {
        updateCount++;
        long timeout = System.currentTimeMillis() + 1_000L;

        while (dayCaptain.getRegisteredUpdates() < updateCount) {
            LockSupport.parkNanos(100_000_000L);
            if (System.currentTimeMillis() > timeout)
                throw new AssertionError("Update hasn't happened within timeout");
        }
        assertThat(dayCaptain.getRegisteredUpdates()).isEqualTo(updateCount);
    }

}
