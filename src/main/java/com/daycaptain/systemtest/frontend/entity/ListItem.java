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

    public static ListItem fromElement(SelenideElement element) {
        ListItem listItem = new ListItem();
        listItem.string = element.$("name").text();
        listItem.hasNote = hasIcon(element, "description");
        listItem.hasRelation = hasIcon(element, "code");
        listItem.hasArea = element.$("div.area").is(exist);
        listItem.project = element.$$("span.project").texts().stream().findAny().orElse(null);

        return listItem;
    }

    static boolean hasIcon(SelenideElement element, String assignment) {
        return !element.$$("i.material-icons").filter(text(assignment)).isEmpty();
    }

}
