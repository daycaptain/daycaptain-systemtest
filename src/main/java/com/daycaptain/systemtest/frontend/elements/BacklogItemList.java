package com.daycaptain.systemtest.frontend.elements;

import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.actions.ConfirmAction;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.entity.BacklogItem;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.daycaptain.systemtest.frontend.views.DynamicView.waitForLoading;
import static com.daycaptain.systemtest.frontend.views.View.press;

public class BacklogItemList extends ListElement {

    public BacklogItemList() {
        super("backlog-items", "backlog-item");
    }

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
        return $$(selector()).stream()
                .map(BacklogItem::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(selector() + " name").stream()
                .map(element -> element.getOwnText().trim())
                .collect(Collectors.toList());
    }

    public void clickDelete(int index) {
        SelenideElement element = hover(index);
        element.$("button.delete").shouldBe(visible).click();
        new ConfirmAction().confirm();
        waitForLoading();
    }

}
