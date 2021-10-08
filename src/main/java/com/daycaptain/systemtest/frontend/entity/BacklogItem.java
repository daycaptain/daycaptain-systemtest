package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

public class BacklogItem extends ListItem {

    public String status;

    protected BacklogItem(SelenideElement element) {
        super(element);
        status = element.getAttribute("class").toUpperCase();
    }

    public static BacklogItem fromElement(SelenideElement element) {
        return new BacklogItem(element);
    }

}
