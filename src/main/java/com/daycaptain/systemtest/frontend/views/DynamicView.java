package com.daycaptain.systemtest.frontend.views;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.actions.SearchAction;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofSeconds;

public abstract class DynamicView extends View {

    public DynamicView() {
        waitForLoading();
    }

    public static void waitForLoading() {
        waitFor(20);
        SelenideElement infoArea = $("info-area");
        Condition loading = cssClass("loading");
        if (!infoArea.has(loading)) {
            waitFor(500);
        }
        infoArea.shouldNotHave(loading, ofSeconds(8));
    }

    public SearchAction search() {
        // might not be available in all views
        press("/");
        return new SearchAction();
    }

}
