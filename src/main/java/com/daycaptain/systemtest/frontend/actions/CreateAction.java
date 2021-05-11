package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.daycaptain.systemtest.frontend.views.View.press;

public class CreateAction extends Action {

    public void setName(String name) {
        String oldTitle = overlay.$("input.edit-name").val();
        press(Keys.ESCAPE + "i");
        for (int i = 0; i < oldTitle.length(); i++)
            press(Keys.BACK_SPACE);
        press(name + Keys.ESCAPE);
    }

    public void setArea(String areaKeyCode) {
        if (areaKeyCode == null) {
            press(Keys.SPACE);
            return;
        }
        press(Keys.ESCAPE + "b");
        press(areaKeyCode.toLowerCase() + Keys.ESCAPE);
    }

    public void setProject(String name) {
        press(Keys.ESCAPE + ",");
        press(name + Keys.ENTER + Keys.ESCAPE);
    }

    public void setNote(String note) {
        press(Keys.ESCAPE + "'");
        press(note + Keys.ESCAPE);
    }

    public String getName() {
        return overlay.$("input.edit-name").val();
    }

    public String getArea() {
        return overlay.$("area-selector").val().trim();
    }

    public String getProject() {
        return overlay.$("project-selector name").val().trim();
    }

    public String getNote() {
        return overlay.$("textarea.note-area").val().trim();
    }

}
