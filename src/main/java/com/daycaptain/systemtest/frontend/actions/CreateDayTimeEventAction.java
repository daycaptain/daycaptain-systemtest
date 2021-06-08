package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.actions;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class CreateDayTimeEventAction extends CreateAction {

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
        return overlay.$(".start-time input").val();
    }

    public String getEndTime() {
        return overlay.$(".end-time input").val();
    }

}
