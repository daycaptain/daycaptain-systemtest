package com.daycaptain.systemtest.backend;

import com.daycaptain.systemtest.backend.entity.*;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

public final class CollectionUtils {

    public static Task findTask(List<Task> list, String name) {
        return list.stream()
                .filter(e -> e.string.equals(name))
                .findAny().orElse(null);
    }

    public static Task findTask(List<Task> list, URI uri) {
        return list.stream()
                .filter(e -> e._self.equals(uri))
                .findAny().orElse(null);
    }

    public static DayTimeEvent findDayTimeEvent(List<DayTimeEvent> list, String name) {
        return list.stream()
                .filter(e -> e.string.equals(name))
                .findAny().orElse(null);
    }

    public static DayTimeEvent findDayTimeEvent(List<DayTimeEvent> list, URI uri) {
        return list.stream()
                .filter(e -> e._self.equals(uri))
                .findAny().orElse(null);
    }

    public static DayEvent findDayEvent(List<DayEvent> list, String name) {
        return list.stream()
                .filter(e -> e.string.equals(name))
                .findAny().orElse(null);
    }

    public static DayEvent findDayEvent(List<DayEvent> list, URI uri) {
        return list.stream()
                .filter(e -> e._self.equals(uri))
                .findAny().orElse(null);
    }

    public static DayEvent findDayEvent(List<DayEvent> list, Predicate<DayEvent> filter) {
        return list.stream()
                .filter(filter)
                .findAny().orElse(null);
    }

    public static Backlog findBacklog(List<Backlog> list, String name) {
        return list.stream()
                .filter(b -> b.name.equals(name))
                .findAny().orElse(null);
    }

    public static BacklogItem findBacklogItem(List<BacklogItem> list, URI uri) {
        return list.stream()
                .filter(b -> b._self.equals(uri))
                .findAny().orElse(null);
    }

    public static BacklogItem findBacklogItem(List<BacklogItem> list, String name) {
        return list.stream()
                .filter(b -> b.string.equals(name))
                .findAny().orElse(null);
    }

    public static Project findProject(List<Project> list, String string) {
        return list.stream()
                .filter(b -> b.string.equals(string))
                .findAny().orElse(null);
    }

    public static Area findArea(List<Area> list, String name) {
        return list.stream()
                .filter(b -> b.name.equals(name))
                .findAny().orElse(null);
    }

}
