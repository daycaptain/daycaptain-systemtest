package com.daycaptain.systemtest.backend.entity;

import java.net.URI;
import java.util.List;

public class Backlog {

    public String name;
    public int priority;
    public String area;
    public String relatedArea;
    public String project;
    public URI _self;
    public List<BacklogItem> items;

}
