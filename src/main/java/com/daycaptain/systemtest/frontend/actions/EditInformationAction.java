package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import static com.daycaptain.systemtest.frontend.views.View.press;

public class EditInformationAction extends EditAction {

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

    public String getArea() {
        return overlay.$("area-selector").text().trim();
    }

    public String getProject() {
        return overlay.$("project-selector name").text().trim();
    }

    public String getNote() {
        return overlay.$("textarea.note-area").text().trim();
    }

}
