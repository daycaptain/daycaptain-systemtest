package com.daycaptain.systemtest.frontend.views;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.actions.DateJump;
import com.daycaptain.systemtest.frontend.actions.SearchAction;
import com.daycaptain.systemtest.frontend.actions.SetFilter;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.*;
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

    public DateJump dateJump() {
        // might not be available in all views
        press("gd");
        return new DateJump();
    }

    public SetFilter filter() {
        press("\\");
        return new SetFilter();
    }

    public String getPageNote() {
        // might not be available in all views
        return $("dp-note textarea").val();
    }

    public void updatePageNote(String note) {
        // might not be available in all views
        press("q'");
        $("dp-note textarea").clear();
        press(Keys.ESCAPE + "'");
        press(note);
        ctrlPress(Keys.ENTER);
        waitForLoading();
    }

    public void assertAreaFilter(String area) {
        $("span.filter area-label").shouldHave(exactOwnText(area));
    }

    public void assertProjectFilter(String area) {
        $("span.filter project-label").shouldHave(exactOwnText(area));
    }

    public void assertNoFilter() {
        $("span.filter").shouldNot(exist);
    }

}
