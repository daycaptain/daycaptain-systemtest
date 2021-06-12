package com.daycaptain.systemtest.frontend.actions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static org.openqa.selenium.Keys.ENTER;

public class DateJump extends Action {

    public DateJump() {
        super();
        waitForLoading();
    }

    public void gotoDate(int dayOfMonth, int month, int yearOfCentury) {
        press(String.valueOf(dayOfMonth));
        press("s");
        press(String.valueOf(month));
        press("d");
        press(String.valueOf(yearOfCentury));
        press(ENTER);
    }

    private void waitForLoading() {
        $("div.overlay date-jump").shouldBe(visible);
    }

}
