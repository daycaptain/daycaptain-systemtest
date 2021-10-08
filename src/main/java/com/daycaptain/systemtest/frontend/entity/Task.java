package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

public class Task extends ListItem {

    public String status;
    public double plannedTime;
    public double assignedTime;

    protected Task(SelenideElement element) {
        super(element);
        status = element.getAttribute("class").toUpperCase();

        String textPieces = element.$("div").text();
        String[] textSplit = textPieces.split(" ");

        if (hasIcon(element, "assignment")) {
            int position = findIconTextPosition(textSplit, "assignment");
            plannedTime = Double.parseDouble(textSplit[position]);
        }

        if (hasIcon(element, "assignment_returned")) {
            int position = findIconTextPosition(textSplit, "assignment_returned");
            assignedTime = Double.parseDouble(textSplit[position]);
        }
    }

    public static Task fromElement(SelenideElement element) {
        return new Task(element);
    }

    private static int findIconTextPosition(String[] pieces, String icon) {
        int position = 0;
        for (int i = pieces.length - 1; i >= 0; i--) {
            if (pieces[i].equals(icon)) {
                position = i + 1;
                break;
            }
        }
        return position;
    }

}
