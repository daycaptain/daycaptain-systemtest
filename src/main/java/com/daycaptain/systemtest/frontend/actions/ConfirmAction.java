package com.daycaptain.systemtest.frontend.actions;

import com.daycaptain.systemtest.frontend.views.DynamicView;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class ConfirmAction extends Action {

    public ConfirmAction() {
        super();
        waitForLoading();
    }

    public void confirm() {
        press(Keys.ENTER);
        DynamicView.waitForLoading();
    }

    public void confirmClick() {
        overlay.$("button.save").click();
        DynamicView.waitForLoading();
    }

    public void cancel() {
        press(Keys.ESCAPE);
    }

    public void cancelClick() {
        overlay.$("button.cancel").click();
    }

    private void waitForLoading() {
        $("div.overlay confirm-action").shouldBe(visible);
    }

}
