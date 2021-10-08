package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

public class TimeEvent extends ListItem {

    public boolean timeZoneSwitch;

    public TimeEvent(SelenideElement element) {
        super(element);
        timeZoneSwitch = hasIcon(element, "access_time");
    }

    public static TimeEvent fromElement(SelenideElement element) {
        return new TimeEvent(element);
    }

}
