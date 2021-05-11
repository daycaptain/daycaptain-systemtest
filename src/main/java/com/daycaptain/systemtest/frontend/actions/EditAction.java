package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.daycaptain.systemtest.frontend.views.View.press;

public class EditAction extends Action {

    public void setName(String name) {
        String oldTitle = overlay.$("input.edit-name").val();
        press(Keys.ESCAPE + "i");
        for (int i = 0; i < oldTitle.length(); i++)
            press(Keys.BACK_SPACE);
        press(name + Keys.ESCAPE);
    }

    public String getName() {
        return overlay.$("input.edit-name").val();
    }

}
