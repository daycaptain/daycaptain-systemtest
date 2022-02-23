package com.daycaptain.systemtest.frontend.views;

import com.daycaptain.systemtest.frontend.elements.ProjectList;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectView extends DynamicView {

    public ProjectList projects() {
        press("p");
        return new ProjectList();
    }

    public ProjectList focusedProjects() {
        $$("dp-project").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new ProjectList();
    }

}
