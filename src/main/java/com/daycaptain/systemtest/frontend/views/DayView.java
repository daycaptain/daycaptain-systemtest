package com.daycaptain.systemtest.frontend.views;

import com.daycaptain.systemtest.frontend.elements.DayEventList;
import com.daycaptain.systemtest.frontend.elements.DayTimeEventList;
import com.daycaptain.systemtest.frontend.elements.TaskList;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;

public class DayView extends DynamicView {

    public DayEventList dayEvents() {
        shiftPress("t");
        return new DayEventList();
    }

    public DayEventList focusedDayEvents() {
        $$("day-event").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new DayEventList();
    }

    public DayTimeEventList timeEvents() {
        press("r");
        return new DayTimeEventList("day-time-events");
    }

    public DayTimeEventList focusedTimeEvents() {
        $$("day-time-event").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new DayTimeEventList("day-time-events");
    }

    public TaskList tasks() {
        press("t");
        return new TaskList("day-tasks");
    }

    public TaskList focusedTaskList() {
        $$("day-tasks dp-task").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new TaskList("day-tasks");
    }

    public WeekView switchToWeek() {
        press("gw");
        return new WeekView();
    }

    public void nextDay() {
        press("ql");
        waitForLoading();
    }

    public void nextDays(int days) {
        press("q" + days + "l");
        waitForLoading();
    }

    public void previousDay() {
        press("qh");
        waitForLoading();
    }

    public String getDateHeader() {
        return $("header.date").text();
    }

    public void assertTourOpen() {
        $("info-area").shouldHave(cssClass("tour-active"));
    }

    public String getTourTooltipText() {
        return $("info-area.tour-active div.tooltip").text();
    }

    public void closeTour() {
        shiftPress(Keys.ESCAPE);
        waitForLoading();
    }

    public void assignTimeEventFromDayTask(int taskIndex, String timeEventName, String startTime, String endTime) {
        tasks().select(taskIndex);
        press("nri");

        for (int i = 0; i < 6; i++)
            ctrlPress(Keys.BACK_SPACE);
        press(timeEventName);

        press(Keys.ESCAPE + "e");
        actions().pause(50).sendKeys(startTime).pause(50).perform();

        press(Keys.ESCAPE + "de");
        actions().pause(50).sendKeys(endTime).pause(50).perform();
        press(Keys.ESCAPE);
        press(Keys.ENTER);

        waitForLoading();
    }

}
