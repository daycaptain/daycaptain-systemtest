package com.daycaptain.systemtest.frontend.actions;

import org.openqa.selenium.Keys;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.daycaptain.systemtest.frontend.views.View.press;
import static java.lang.Math.abs;

public class CreateTaskAction extends CreateAction {

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

    public void setRepeated(int repeated, int cadence) {
        press(Keys.ESCAPE + "r");
        press("l".repeat(repeated));
        if (cadence > 0)
            press("k".repeat(cadence - 1));
        press(Keys.ESCAPE);
    }

}
