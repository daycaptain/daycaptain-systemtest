package com.daycaptain.systemtest.backend.entity;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    public List<BacklogItem> backlogItems = new ArrayList<>();
    public List<Task> dayTasks = new ArrayList<>();
    public List<Task> weekTasks = new ArrayList<>();
    public List<DayTimeEvent> timeEvents = new ArrayList<>();
    public List<DayEvent> dayEvents = new ArrayList<>();
    public List<Project> projects = new ArrayList<>();

}
