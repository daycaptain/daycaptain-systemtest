package com.daycaptain.systemtest.frontend.actions;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class EditBacklogItemAction extends EditInformationAction {

    public EditBacklogItemAction() {
        waitForLoading();
    }

    public List<String> getRelationNames() {
        return overlay.$$("dp-relations dp-relation").texts();
    }

    protected void waitForLoading() {
        $("dp-relations div.loading").shouldNotBe(visible);
    }

}
