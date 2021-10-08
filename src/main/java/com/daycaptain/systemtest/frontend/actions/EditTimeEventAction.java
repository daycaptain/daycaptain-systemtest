package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import java.time.ZoneId;
import java.util.List;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.actions;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class EditTimeEventAction extends EditInformationAction {

    public EditTimeEventAction() {
        waitForLoading();
    }

    public void setStartTime(String startTime) {
        press(Keys.ESCAPE + "e");
        actions().pause(50).sendKeys(startTime).pause(50).perform();
        press(Keys.ESCAPE);
    }

    public void setEndTime(String endTime) {
        press(Keys.ESCAPE + "de");
        actions().pause(50).sendKeys(endTime).pause(50).perform();
        press(Keys.ESCAPE);
    }

    public String getStartTime() {
        return overlay.$(".start-time input").getAttribute("value");
    }

    public String getEndTime() {
        return overlay.$(".end-time input").getAttribute("value");
    }

    public List<String> getRelationNames() {
        return overlay.$$("dp-relations dp-relation").texts();
    }

    protected void waitForLoading() {
        overlay.$("edit-time-event").shouldBe(visible);
        overlay.$("dp-relations div.loading").shouldNotBe(visible);
    }

    public void assertTimeZone(ZoneId zone) {
        press(Keys.ESCAPE);
        overlay.$(".time-zone-label").shouldHave(ownText(zone.toString()));
    }

}
