package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;

public class ProjectList extends ListElement {

    public ProjectList() {
        super("dp-projects", "dp-project");
    }

    public List<ListItem> getList() {
        return $$(selector()).stream()
                .map(ListItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(selector()).texts();
    }

    public ListItem focused() {
        $$(itemCssSelector).should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return ListItem.fromElement($(getFocusedElement()));
    }

}
