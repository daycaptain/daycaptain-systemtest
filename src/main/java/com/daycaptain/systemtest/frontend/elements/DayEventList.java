package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditDayEventAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class DayEventList extends ListElement {

    public DayEventList() {
        super("day-events", "day-event");
    }

    public CreateDayTimeEventAction create() {
        press("nn");
        return new CreateDayTimeEventAction();
    }

    public EditDayEventAction edit() {
        press("e");
        return new EditDayEventAction();
    }

    public EditDayEventAction edit(int index) {
        select(index);
        press("e");
        return new EditDayEventAction();
    }

    public List<ListItem> getList() {
        return $$(selector()).stream()
                .map(ListItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(selector()).texts();
    }

}
