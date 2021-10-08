package com.daycaptain.systemtest.backend.entity;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day {

    public List<Task> tasks = new ArrayList<>();

    public List<DayEvent> dayEvents = new ArrayList<>();

    public List<DayTimeEvent> timeEvents = new ArrayList<>();

    // to preserve order
    public DayStatistics statistics;

    public String note;

    public String prevZone;

    public static class DayStatistics {

        public Statistics planned;
        public Statistics actual;

        public static class Statistics {

            public JsonObject _areas;
            public JsonObject _projects;

            public JsonObject getAreas() {
                return _areas;
            }

            public void setAreas(JsonObject areas) {
                this._areas = areas;
                this.areas = new Statistic(areas);
            }

            public JsonObject getProjects() {
                return _projects;
            }

            public void setProjects(JsonObject projects) {
                this._projects = projects;
                this.projects = new Statistic(projects);
            }

            public Statistic areas;
            public Statistic projects;
        }

        public static class Statistic {

            public final List<String> keys = new ArrayList<>();
            public final Map<String, Integer> minutes = new HashMap<>();

            public Statistic(JsonObject json) {
                json.forEach((key, value) -> {
                    keys.add(key);
                    minutes.put(key, ((JsonNumber) value).intValue());
                });
            }
        }

    }

}