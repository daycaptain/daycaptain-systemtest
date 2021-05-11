package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class DayTimeEventList extends ListElement {

    private final String cssSelector;

    public DayTimeEventList(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    public CreateDayTimeEventAction create() {
        press("nn");
        return new CreateDayTimeEventAction();
    }

    public EditTimeEventAction edit() {
        press("e");
        return new EditTimeEventAction();
    }

    public EditTimeEventAction edit(int index) {
        select(index);
        press("e");
        return new EditTimeEventAction();
    }

    public List<ListItem> getList() {
        return $$(cssSelector + " day-time-event").stream()
                .map(ListItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(cssSelector + " day-time-event").texts();
    }

    public ListItem focused() {
        $$("day-time-event").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return ListItem.fromElement($(getFocusedElement()));
    }

}
