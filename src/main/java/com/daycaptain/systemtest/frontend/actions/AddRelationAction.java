package com.daycaptain.systemtest.frontend.actions;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class AddRelationAction extends Action {

    public AddRelationAction() {
        waitForLoading();
    }

    public void setSearchTerm(String searchTerm) {
        press(searchTerm);
        waitForLoading();
    }

    protected void waitForLoading() {
        overlay.$("add-relation").shouldBe(visible);
        overlay.$(".loading").shouldNot(exist);
    }

}
