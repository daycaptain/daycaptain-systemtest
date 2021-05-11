package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.entity.BacklogItem;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class BacklogItemList extends ListElement {

    public EditBacklogItemAction edit() {
        press("e");
        return new EditBacklogItemAction();
    }

    public EditBacklogItemAction edit(int index) {
        select(index);
        press("e");
        return new EditBacklogItemAction();
    }

    public List<BacklogItem> getList() {
        return $$("dp-backlog backlog-item").stream()
                .map(BacklogItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$("dp-backlog backlog-item").texts();
    }

}
