package com.daycaptain.systemtest.frontend.actions;

import static com.codeborne.selenide.Condition.visible;

public class EditBacklogAction extends EditAction {

    public EditBacklogAction() {
        waitForLoading();
    }

    protected void waitForLoading() {
        overlay.$("edit-backlog").shouldBe(visible);
    }

}
