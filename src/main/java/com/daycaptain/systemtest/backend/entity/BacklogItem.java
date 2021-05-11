package com.daycaptain.systemtest.backend.entity;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class BacklogItem {

    public String string;
    public boolean archived;
    public String status;
    public int priority;
    public String note;
    public String area;
    public String relatedArea;
    public String project;
    public String relatedProject;
    public Set<URI> assignedTasks = new HashSet<>();
    public URI _self;
    public URI backlog;

}
