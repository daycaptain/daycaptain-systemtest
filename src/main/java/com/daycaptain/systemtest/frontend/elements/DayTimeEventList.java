package com.daycaptain.systemtest.frontend.elements;

import com.daycaptain.systemtest.frontend.actions.CreateDayTimeEventAction;
import com.daycaptain.systemtest.frontend.actions.EditTimeEventAction;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import com.daycaptain.systemtest.frontend.entity.TimeEvent;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static org.assertj.core.api.Assertions.assertThat;

public class DayTimeEventList extends ListElement {

    public DayTimeEventList(String cssSelector) {
        super(cssSelector, "day-time-event");
    }

    public CreateDayTimeEventAction create() {
        press("nn");
        return new CreateDayTimeEventAction();
    }

    public void createSave(String name, String startTime, String endTime) {
        CreateDayTimeEventAction action = create();
        action.setName(name);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.save();
    }

    public void createSave(String name, ZoneId startTimeZone, ZoneId endTimeZone, String startTime, String endTime) {
        CreateDayTimeEventAction action = create();
        action.setName(name);
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.save();
    }

    public void createSave(String name, String startTime, String endTime, ZoneId startTimeZone, ZoneId endTimeZone) {
        CreateDayTimeEventAction action = create();
        action.setName(name);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.save();
    }

    public EditTimeEventAction edit() {
        press("e");
        return new EditTimeEventAction();
    }

    public void editSave(ZoneId startTimeZone, ZoneId endTimeZone, String startTime, String endTime) {
        EditTimeEventAction action = edit();
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.save();
    }

    public void editSave(String startTime, String endTime, ZoneId startTimeZone, ZoneId endTimeZone) {
        EditTimeEventAction action = edit();
        action.setStartTime(startTime);
        action.setEndTime(endTime);
        action.setStartTimeZone(startTimeZone);
        action.setEndTimeZone(endTimeZone);
        action.save();
    }

    public EditTimeEventAction edit(int index) {
        select(index);
        press("e");
        return new EditTimeEventAction();
    }

    public void assertEdit(String startTime, String endTime, ZoneId zones) {
        EditTimeEventAction action = edit();
        assertThat(action.getStartTime()).isEqualTo(startTime);
        assertThat(action.getEndTime()).isEqualTo(endTime);
        action.assertTimeZone(zones);
        action.close();
    }

    public void assertEdit(String startTime, String endTime, ZoneId startZone, ZoneId endZone) {
        EditTimeEventAction action = edit();
        assertThat(action.getStartTime()).isEqualTo(startTime);
        assertThat(action.getEndTime()).isEqualTo(endTime);
        action.assertStartTimeZone(startZone);
        action.assertEndTimeZone(endZone);
        action.close();
    }

    public List<TimeEvent> getList() {
        return $$(selector()).stream()
                .map(TimeEvent::fromElement)
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return $$(selector()).texts();
    }

    public ListItem focused() {
        $$(itemCssSelector).should(anyMatch("Either of the elements has to be focused", el -> $(el).is(focused)));
        return ListItem.fromElement($(getFocusedElement()));
    }

}
