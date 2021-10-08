package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;

public class ListItem {

    public String string;
    public boolean hasNote;
    public boolean hasRelation;
    public boolean hasArea;
    public String project;

    protected ListItem(SelenideElement element) {
        string = element.$("name").text();
        hasNote = hasIcon(element, "description");
        hasRelation = hasIcon(element, "code");
        hasArea = element.$("div.area").is(exist);
        project = element.$$("span.project").texts().stream().findAny().orElse(null);
    }

    public static ListItem fromElement(SelenideElement element) {
        return new ListItem(element);
    }

    static boolean hasIcon(SelenideElement element, String assignment) {
        return !element.$$("i.material-icons").filter(text(assignment)).isEmpty();
    }

}
