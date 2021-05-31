package com.daycaptain.systemtest.frontend.views;

import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.actions.EditBacklogItemAction;
import com.daycaptain.systemtest.frontend.elements.BacklogItemList;
import com.daycaptain.systemtest.frontend.elements.BacklogList;
import com.daycaptain.systemtest.frontend.entity.BacklogItem;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;

public class BacklogsView extends DynamicView {

    public BacklogList backlogList() {
        press("h");
        return new BacklogList();
    }

    public BacklogList focusedBacklogList() {
        $$("dp-backlog").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new BacklogList();
    }

    public BacklogItemList backlogItemList() {
        press("l");
        return new BacklogItemList();
    }

    public BacklogItemList focusedBacklogItemList() {
        $$("backlog-item").should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return new BacklogItemList();
    }

    public List<String> getCurrentBacklogItemNames() {
        return $$("backlog-items backlog-item name").stream()
                .map(SelenideElement::text)
                .collect(Collectors.toList());
    }

    public List<BacklogItem> getCurrentBacklogItems() {
        return $$("backlog-items backlog-item").stream()
                .map(BacklogItem::fromElement)
                .collect(Collectors.toList());
    }

    public void selectInbox() {
        press("hgg");
        waitForLoading();
    }

    public void selectBacklog(String backlogName) {
        press("hgg");
        while (!$(getFocusedElement()).$("name").text().equals(backlogName))
            press("j");
        waitForLoading();
    }

    public void createInboxItem(String taskName) {
        press("hgglnn");
        press(taskName + Keys.ENTER);
        waitForLoading();
    }

    public void createInboxItemWithArea(String taskName, String areaKey) {
        press("hgglnn");
        press(taskName + Keys.ESCAPE);
        press("b" + areaKey + Keys.ESCAPE + Keys.ENTER);
        waitForLoading();
    }

    public void createInboxItemWithProject(String taskName, String projectName) {
        press("hgglnn");
        press(taskName + Keys.ESCAPE);
        press("," + projectName + Keys.ENTER + Keys.ENTER);
        waitForLoading();
    }

    public void createBacklogItem(String taskName, String backlogName) {
        selectBacklog(backlogName);
        press("lnn");
        press(taskName + Keys.ENTER);
        waitForLoading();
    }

    public EditBacklogItemAction editBacklogItem(int backlogItemIndex) {
        selectBacklogItem(backlogItemIndex);
        press("e");
        return new EditBacklogItemAction();
    }

    public void assignDayTaskFromBacklogItem(int backlogItemIndex, String dayTaskName, int dayOffset) {
        assignTaskFromBacklogItem(backlogItemIndex, dayTaskName, dayOffset, key().sendKeys("nt"));
    }

    public void assignWeekTaskFromBacklogItem(int backlogItemIndex, String weekTaskName, int weekOffset) {
        Actions keys = key().keyDown(Keys.SHIFT)
                .sendKeys("nt")
                .keyUp(Keys.SHIFT);
        assignTaskFromBacklogItem(backlogItemIndex, weekTaskName, weekOffset, keys);
    }

    private void assignTaskFromBacklogItem(int backlogItemIndex, String dayTaskName, int temporalOffset, Actions keyShortcut) {
        selectBacklogItem(backlogItemIndex);
        keyShortcut.perform();

        String oldTitle = $("input.edit-name").val();
        press(Keys.ESCAPE + "i");
        for (int i = 0; i < oldTitle.length(); i++)
            press(Keys.BACK_SPACE);
        press(dayTaskName);

        press(Keys.ESCAPE + "s");

        String key = temporalOffset < 0 ? "h" : "l";
        for (int i = 0; i < Math.abs(temporalOffset); i++)
            press(key);

        press(Keys.ENTER);
        waitForLoading();
    }

    private void selectBacklogItem(int index) {
        press("lgg");
        for (int i = 0; i < index; i++)
            press("j");
    }
}
