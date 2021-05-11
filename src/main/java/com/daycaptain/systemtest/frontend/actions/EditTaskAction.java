package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static java.lang.Math.abs;

public class EditTaskAction extends EditInformationAction {

    public EditTaskAction() {
        waitForLoading();
    }

    public void setPlannedTime(int timeUnits, boolean useJump) {
        press(Keys.ESCAPE);
        String moveDirectionKey = timeUnits < 0 ? "h" : "l";
        String keys = useJump ? abs(timeUnits) + moveDirectionKey : moveDirectionKey.repeat(abs(timeUnits));
        press(keys);
    }

    public String getPlannedTime() {
        String text = overlay.$("span.task-planned").text().trim();
        Pattern pattern = Pattern.compile(".*\\s(\\d{1,2}.\\d{1,2})\\shours\\s.*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
            return matcher.replaceFirst("$1");
        return null;
    }

    public List<String> getRelationNames() {
        return overlay.$$("dp-relations dp-relation").texts();
    }

    protected void waitForLoading() {
        overlay.$("edit-task").shouldBe(visible);
        overlay.$("dp-relations div.loading").shouldNotBe(visible);
    }

}
