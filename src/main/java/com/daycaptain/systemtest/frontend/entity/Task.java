package com.daycaptain.systemtest.frontend.entity;

import com.codeborne.selenide.SelenideElement;

public class Task extends ListItem {

    public String status;
    public double plannedTime;
    public double assignedTime;

    public static Task fromElement(SelenideElement element) {
        ListItem listItem = ListItem.fromElement(element);
        Task task = new Task();
        task.string = listItem.string;
        task.project = listItem.project;
        task.hasArea = listItem.hasArea;
        task.hasNote = listItem.hasNote;
        task.hasRelation = listItem.hasRelation;

        task.status = element.getAttribute("class").toUpperCase();

        String textPieces = element.$("div").text();
        String[] textSplit = textPieces.split(" ");

        if (hasIcon(element, "assignment")) {
            int position = findIconTextPosition(textSplit, "assignment");
            task.plannedTime = Double.parseDouble(textSplit[position]);
        }

        if (hasIcon(element, "assignment_returned")) {
            int position = findIconTextPosition(textSplit, "assignment_returned");
            task.assignedTime = Double.parseDouble(textSplit[position]);
        }

        return task;
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
