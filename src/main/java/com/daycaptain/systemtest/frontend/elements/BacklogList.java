package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.actions.EditBacklogAction;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class BacklogList {

    public ListItem focusedItem() {
        $$("dp-backlog").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return ListItem.fromElement($(getFocusedElement()));
    }

    public EditBacklogAction edit() {
        press("e");
        return new EditBacklogAction();
    }

    public List<ListItem> getList() {
        return $$("dp-backlogs dp-backlog").stream()
                .map(ListItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$("dp-backlogs dp-backlog").texts();
    }

}
