package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.views.DynamicView;

import java.util.List;

import static com.daycaptain.systemtest.frontend.views.View.ctrlPress;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static java.lang.Math.abs;

public abstract class ListElement {

    public void select(int index) {
        select(index, false);
    }

    private void select(int index, boolean useJump) {
        press("gg");
        if (index > 0) {
            String keySequence = useJump ? index + "j" : "j".repeat(index);
            press(keySequence);
        }
    }

    public void create(String name) {
        CreateAction createAction = create();
        createAction.setName(name);
        createAction.save();
    }

    public CreateAction create() {
        press("nn");
        return new CreateAction();
    }

    public EditInformationAction edit(int index) {
        select(index);
        press("e");
        return new EditInformationAction();
    }

    public void delete(int index) {
        select(index);
        press("dd");
        DynamicView.waitForLoading();
    }

    public void move(int index, int moveDistance) {
        move(index, moveDistance, false);
    }

    public void move(int index, int moveDistance, boolean useJump) {
        select(index, useJump);
        String moveDirectionKey = moveDistance < 0 ? "k" : "j";
        String keySequence = useJump ? abs(moveDistance) + moveDirectionKey : moveDirectionKey.repeat(abs(moveDistance));
        ctrlPress(keySequence);
        DynamicView.waitForLoading();
    }

    public abstract List<? extends ListItem> getList();

}
