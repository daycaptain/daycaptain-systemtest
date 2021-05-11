package com.daycaptain.systemtest.backend.entity;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class Task {

    public String string;
    public String status;
    public String dueTime;
    public int planned;
    public int assigned;
    public int current;
    public int priority;
    public String note;
    public String area;
    public String relatedArea;
    public String project;
    public String relatedProject;
    public URI assignedFromWeekTask;
    public URI assignedFromBacklogTask;
    public Set<URI> assignedTasks = new HashSet<>();
    public Set<URI> assignedDayTimeEvents = new HashSet<>();
    public URI _self;

}
