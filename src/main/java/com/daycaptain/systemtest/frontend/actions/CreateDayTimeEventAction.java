package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import java.time.ZoneId;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.actions;
import static com.daycaptain.systemtest.frontend.views.View.*;

public class CreateDayTimeEventAction extends CreateAction {

    public void setZones(ZoneId zone) {
        press(Keys.ESCAPE + "t");
        actions().pause(50).sendKeys(zone.toString()).pause(50).perform();
        press(Keys.ENTER);
        waitFor(50);
    }

    public void setStartTime(String startTime) {
        press(Keys.ESCAPE + "e");
        actions().pause(50).sendKeys(startTime).pause(50).perform();
        press(Keys.ESCAPE);
    }

    public void setStartTimeZone(ZoneId zone) {
        press(Keys.ESCAPE);
        shiftPress("t");
        actions().pause(50).sendKeys(zone.toString()).pause(50).perform();
        press(Keys.ENTER);
        waitFor(50);
    }

    public void setEndTime(String endTime) {
        press(Keys.ESCAPE + "de");
        actions().pause(50).sendKeys(endTime).pause(50).perform();
        press(Keys.ESCAPE);
    }

    public void setEndTimeZone(ZoneId zone) {
        press(Keys.ESCAPE + "d");
        shiftPress("t");
        actions().pause(50).sendKeys(zone.toString()).pause(50).perform();
        press(Keys.ENTER);
        waitFor(50);
    }

    public void assertStartTime(String time) {
        overlay.$(".start-time input").shouldHave(value(time));
    }

    public void assertEndTime(String time) {
        overlay.$(".end-time input").shouldHave(value(time));
    }

    public void assertTimeZone(ZoneId zone) {
        press(Keys.ESCAPE);
        overlay.$("zone-selector.zones name").shouldHave(ownText(zone.toString()));
    }

}
