package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.value;
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

    public void assertStartTime(String time) {
        overlay.$(".start-time input").shouldHave(value(time));
    }

    public void assertEndTime(String time) {
        overlay.$(".end-time input").shouldHave(value(time));
    }

}
