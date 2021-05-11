package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.daycaptain.systemtest.frontend.views.View.press;

public class CreateDayTimeEventAction extends CreateAction {

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
        return overlay.$(".start-time input").val();
    }

    public String getEndTime() {
        return overlay.$(".end-time input").val();
    }

}
