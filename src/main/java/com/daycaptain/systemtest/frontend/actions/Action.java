package com.daycaptain.systemtest.frontend.actions;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.daycaptain.systemtest.frontend.views.DynamicView.waitForLoading;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static java.time.Duration.ofSeconds;

public abstract class Action {

    protected final SelenideElement overlay;

    public Action() {
        overlay = $(".overlay");
        overlay.shouldBe(visible, ofSeconds(8));
    }

    public void save() {
        press(Keys.ENTER);
        waitForLoading();
    }

    public void close() {
        overlay.$("button.close").click();
    }
}
