package com.daycaptain.systemtest.frontend.elements;

import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.actions.AddRelationAction;
import com.daycaptain.systemtest.frontend.actions.ConfirmAction;
import com.daycaptain.systemtest.frontend.actions.CreateTaskAction;
import com.daycaptain.systemtest.frontend.actions.EditTaskAction;
import com.daycaptain.systemtest.frontend.entity.Task;
import org.openqa.selenium.Keys;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.DynamicView.waitForLoading;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static com.daycaptain.systemtest.frontend.views.View.shiftPress;
import static java.lang.Math.abs;

public class TaskList extends ListElement {

    public TaskList(String cssSelector) {
        super(cssSelector, "dp-task");
    }

    public CreateTaskAction create() {
        press("nn");
        return new CreateTaskAction();
    }

    public EditTaskAction edit() {
        press("e");
        return new EditTaskAction();
    }

    public EditTaskAction edit(int index) {
        select(index);
        press("e");
        return new EditTaskAction();
    }

    public EditTaskAction editLast() {
        shiftPress("g");
        press("e");
        return new EditTaskAction();
    }

    public void editPlannedTime(int index, int timeUnits) {
        editPlannedTime(index, timeUnits, false);
    }

    public void editPlannedTime(int index, int timeUnits, boolean useJump) {
        select(index);

        String moveDirectionKey = timeUnits < 0 ? "h" : "l";
        // why is there a max?
        String keys = useJump ? abs(timeUnits) + moveDirectionKey : moveDirectionKey.repeat(Math.max(0, abs(timeUnits)));
        shiftPress(keys);
        waitForLoading();
    }

    public void finish(int index) {
        select(index);
        press(Keys.ENTER);
        waitForLoading();
    }

    public void undoStatus(int index) {
        select(index);
        press(Keys.BACK_SPACE);
        waitForLoading();
    }

    public void cancel(int index) {
        select(index);
        press("x");
        waitForLoading();
    }

    public List<Task> getList() {
        return $$(selector()).stream()
                .map(Task::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(selector() + " name").texts();
    }

    public Task focused() {
        $$(itemCssSelector).should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return Task.fromElement($(getFocusedElement()));
    }

    public void connectToItem(String name, int index) {
        select(index);
        press("f");
        AddRelationAction action = new AddRelationAction();
        action.setSearchTerm(name);
        action.save();
    }

    public void clickDelete(int index) {
        SelenideElement element = hover(index);
        element.$("button.delete").shouldBe(visible).click();
        new ConfirmAction().confirm();
        waitForLoading();
    }

}
