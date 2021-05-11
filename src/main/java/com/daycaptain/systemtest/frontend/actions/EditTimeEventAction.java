package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class EditTimeEventAction extends EditInformationAction {

    public EditTimeEventAction() {
        waitForLoading();
    }

    public void setStartTime(String startTime) {
        press(Keys.ESCAPE + "e");
        for (int i = 0; i < 5; i++)
            press(Keys.BACK_SPACE);
        press(startTime);
    }

    public void setEndTime(String endTime) {
        press(Keys.ESCAPE + "de");
        for (int i = 0; i < 5; i++)
            press(Keys.BACK_SPACE);
        press(endTime);
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

}
