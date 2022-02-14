package com.daycaptain.systemtest.frontend.actions;

import com.daycaptain.systemtest.frontend.views.DynamicView;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class SetFilter extends Action {

    public SetFilter() {
        super();
        waitForLoading();
    }

    public void area(String name) {
        String keyCode = $$("div.overlay set-filter > div:first-of-type name")
                .filter(exactText(name))
                .first()
                .parent().$("span.kbd kbd")
                .text().toLowerCase();

        press(keyCode);
        DynamicView.waitForLoading();
    }

    public void project(String name) {
        press(",");
        press(name + Keys.ENTER);
        DynamicView.waitForLoading();
    }

    public void none() {
        press(Keys.SPACE);
        DynamicView.waitForLoading();
    }

    private void waitForLoading() {
        $("div.overlay set-filter").shouldBe(visible);
    }

}
