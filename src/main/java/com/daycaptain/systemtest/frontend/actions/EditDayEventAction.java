package com.daycaptain.systemtest.frontend.actions;

import static com.codeborne.selenide.Condition.visible;

public class EditDayEventAction extends EditInformationAction {

    public EditDayEventAction() {
        waitForLoading();
    }

    public String getStartDate() {
        return overlay.$(".start-date div").getText();
    }

    public String getEndTime() {
        return overlay.$(".end-date div").getText();
    }

    protected void waitForLoading() {
        overlay.$("edit-day-event").shouldBe(visible);
    }

}
