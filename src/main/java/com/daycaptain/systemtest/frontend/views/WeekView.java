package com.daycaptain.systemtest.frontend.views;

import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import org.openqa.selenium.Keys;

import java.time.DayOfWeek;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getFocusedElement;

public class WeekView extends DynamicView {

    public String getDateHeader() {
        return $("header.week-header").text();
    }

    public DayTimeEventList dayTimeEvents(DayOfWeek dayOfWeek) {
        press("gwr");
        // doesn't work if week is YearWeek.now()
        String keySequence = "l".repeat(dayOfWeek.ordinal());
        press(keySequence);
        int index = dayOfWeek.ordinal() + 1;
        return new DayTimeEventList(".week-grid > div:nth-of-type(" + index + ") day-time-events");
    }

    public DayTimeEventList dayTimeEventsOffset(int temporalOffset) {
        press("gwr");
        String key = temporalOffset < 0 ? "h" : "l";
        for (int i = 0; i < Math.abs(temporalOffset); i++)
            press(key);
        return new DayTimeEventList(".week-grid > div.selected day-time-events");
    }

    public TaskList weekTasks() {
        press("gw");
        return new TaskList("week-tasks");
    }

    public TaskList focusedWeekTasks() {
        // expects that task list is focused
        String activeTag = getFocusedElement().getTagName();
        if (!"week-tasks".equals(activeTag) && !"dp-task".equals(activeTag))
            throw new IllegalStateException("Expected week tasks to be focused, but active element is: " + activeTag);
        return new TaskList("week-tasks");
    }

    public TaskList dayTasks(DayOfWeek dayOfWeek) {
        press("gwt");
        // doesn't work if week is YearWeek.now()
        String keySequence = "l".repeat(dayOfWeek.ordinal());
        press(keySequence);
        int index = dayOfWeek.ordinal() + 1;
        return new TaskList(".week-grid > div:nth-of-type(" + index + ") day-tasks");
    }

    public TaskList dayTasksOffset(int temporalOffset) {
        press("gwt");
        String key = temporalOffset < 0 ? "h" : "l";
        for (int i = 0; i < Math.abs(temporalOffset); i++)
            press(key);
        return new TaskList(".week-grid > div.selected day-tasks");
    }

    public String selectedDay() {
        return $(".week-grid div.selected > header").text();
    }

    public void assignDayTaskFromWeekTask(int weekTaskIndex, String dayTaskName, DayOfWeek dayOfWeek) {
        weekTasks().select(weekTaskIndex);
        press("nti");

        for (int i = 0; i < 6; i++)
            ctrlPress(Keys.BACK_SPACE);
        press(dayTaskName);

        press(Keys.ESCAPE + "s");

        // doesn't work if week is YearWeek.now()
        for (int i = 0; i < dayOfWeek.ordinal(); i++)
            press("l");

        press(Keys.ENTER);
        waitForLoading();
    }

    public void assignDayTaskFromWeekTask(int weekTaskIndex, String dayTaskName, int temporalOffset) {
        weekTasks().select(weekTaskIndex);
        press("nti");

        for (int i = 0; i < 6; i++)
            ctrlPress(Keys.BACK_SPACE);
        press(dayTaskName);

        press(Keys.ESCAPE + "s");


        String key = temporalOffset < 0 ? "h" : "l";
        for (int i = 0; i < Math.abs(temporalOffset); i++)
            press(key);

        press(Keys.ENTER);
        waitForLoading();
    }

}
