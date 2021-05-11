package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

public class BacklogItem extends ListItem {

    public String status;

    public static BacklogItem fromElement(SelenideElement element) {
        ListItem listItem = ListItem.fromElement(element);
        BacklogItem item = new BacklogItem();
        item.string = listItem.string;
        item.project = listItem.project;
        item.hasArea = listItem.hasArea;
        item.hasNote = listItem.hasNote;
        item.hasRelation = listItem.hasRelation;

        item.status = element.getAttribute("class").toUpperCase();

        return item;
    }
}
