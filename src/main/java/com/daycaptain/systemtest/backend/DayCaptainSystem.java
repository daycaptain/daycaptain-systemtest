package com.daycaptain.systemtest.backend;

import com.daycaptain.systemtest.backend.entity.*;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.threeten.extra.YearWeek;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DayCaptainSystem {

    private final Client client;
    private final WebTarget rootTarget;
    private SseEventSource updateSource;
    private int updateCounter;

    public DayCaptainSystem() {
        // required for PATCH
        client = ClientBuilder.newClient()
                .register(CookieAuthenticationFilter.class)
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        rootTarget = client.target(buildUri());
    }

    private URI buildUri() {
        String host = System.getProperty("daycaptain.test.host", "localhost");
        String port = System.getProperty("daycaptain.test.port", "8080");
        return UriBuilder.fromUri("http://{host}:{port}/").build(host, port);
    }

    public List<Area> getAreas() {
        Response response = rootTarget.path("areas")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        verifySuccess(response);
        GenericType<List<Area>> listType = new GenericType<>() {
        };
        return response.readEntity(listType);
    }

    public Area getArea(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(Area.class);
    }

    public URI createArea(String name) {
        JsonObject entity = Json.createObjectBuilder()
                .add("name", name)
                .build();

        return requestCreateVerify(entity, "areas");
    }

    public void updateArea(Area area, String property, Object value) {
        JsonObject patch = patch(property, value);

        Response response = requestUpdate(area._self, patch);
        verifySuccess(response);
    }

    public void migrateAreaToProject(Area area) {
        URI uri = UriBuilder.fromUri(area._self).path("migrations").build();
        Response response = client.target(uri).request()
                .post(Entity.json(null));
        verifySuccess(response);
    }

    public void migrateProjectToArea(Project project) {
        URI uri = UriBuilder.fromUri(project._self).path("migrations").build();
        Response response = client.target(uri).request()
                .post(Entity.json(null));
        verifySuccess(response);
    }

    public void deleteArea(Area area) {
        Response response = requestDelete(area._self);
        verifySuccess(response);
    }

    public Week getWeek(YearWeek week) {
        Response response = requestWeek(week, null, null);
        verifySuccess(response);
        return response.readEntity(Week.class);
    }

    public Week getWeekFilterArea(YearWeek week, String areaFilter) {
        Response response = requestWeek(week, "area", areaFilter);
        verifySuccess(response);
        return response.readEntity(Week.class);
    }

    public Week getWeekFilterProject(YearWeek week, String projectFilter) {
        Response response = requestWeek(week, "project", projectFilter);
        verifySuccess(response);
        return response.readEntity(Week.class);
    }

    public Week getWeekWithDailyNotes(YearWeek week) {
        Response response = requestWeek(week, "notes", "day");
        verifySuccess(response);
        return response.readEntity(Week.class);
    }

    public Day getDay(LocalDate date) {
        Response response = requestDay(date.toString());
        verifySuccess(response);
        return response.readEntity(Day.class);
    }

    public Day getDayFilterArea(LocalDate date, String areaFilter) {
        Response response = requestDayFilterArea(date.toString(), areaFilter);
        verifySuccess(response);
        return response.readEntity(Day.class);
    }

    public Day getDayFilterProject(LocalDate date, String projectFilter) {
        Response response = requestDayFilterProject(date.toString(), projectFilter);
        verifySuccess(response);
        return response.readEntity(Day.class);
    }

    public Task getTask(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(Task.class);
    }

    public Backlog getInbox() {
        Response response = requestInbox(null, null, false);
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getInboxFilterArea(String areaFilter) {
        Response response = requestInbox(areaFilter, null, false);
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getInboxFilterProject(String projectFilter) {
        Response response = requestInbox(null, projectFilter, false);
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getInbox(boolean archived) {
        Response response = requestInbox(null, null, archived);
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public List<Backlog> getBacklogs() {
        Response response = requestBacklogs(null, null);
        verifySuccess(response);
        GenericType<List<Backlog>> listType = new GenericType<>() {
        };
        return response.readEntity(listType);
    }

    public List<Backlog> getBacklogsFilterArea(String areaFilter) {
        Response response = requestBacklogs(areaFilter, null);
        verifySuccess(response);
        GenericType<List<Backlog>> listType = new GenericType<>() {
        };
        return response.readEntity(listType);
    }

    public List<Backlog> getBacklogsFilterProject(String projectFilter) {
        Response response = requestBacklogs(null, projectFilter);
        verifySuccess(response);
        GenericType<List<Backlog>> listType = new GenericType<>() {
        };
        return response.readEntity(listType);
    }

    public Backlog getBacklog(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getBacklogFilterArea(URI uri, String areaFilter) {
        Response response = request(UriBuilder
                .fromUri(uri)
                .queryParam("area", areaFilter)
                .build());
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getBacklogFilterProject(URI uri, String projectFilter) {
        Response response = request(UriBuilder
                .fromUri(uri)
                .queryParam("project", projectFilter)
                .build());
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public Backlog getBacklog(URI uri, boolean archived) {
        Response response = request(UriBuilder
                .fromUri(uri)
                .queryParam("archived", String.valueOf(archived))
                .build());
        verifySuccess(response);
        return response.readEntity(Backlog.class);
    }

    public BacklogItem getBacklogItem(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(BacklogItem.class);
    }

    private Response requestWeek(YearWeek week, String queryParam, Object queryValue) {
        WebTarget target = rootTarget.path(week.toString());
        if (queryParam != null)
            target = target.queryParam(queryParam, queryValue);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    public DayEvent getDayEvent(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(DayEvent.class);
    }

    public DayTimeEvent getDayTimeEvent(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(DayTimeEvent.class);
    }

    private Response request(URI uri) {
        return client.target(uri)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
    }

    private Response requestDay(String date) {
        WebTarget target = rootTarget.path(date);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    private Response requestDayFilterArea(String date, String areaFilter) {
        return rootTarget
                .path(date)
                .queryParam("area", areaFilter)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    private Response requestDayFilterProject(String date, String projectFilter) {
        return rootTarget
                .path(date)
                .queryParam("project", projectFilter)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    private Response requestInbox(String areaFilter, String projectFilter, boolean archived) {
        WebTarget target = rootTarget.path("backlogs/inbox");
        if (areaFilter != null)
            target = target.queryParam("area", areaFilter);
        if (projectFilter != null)
            target = target.queryParam("project", projectFilter);
        if (archived)
            target = target.queryParam("archived", "true");
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    private Response requestBacklogs(String areaFilter, String projectFilter) {
        WebTarget target = rootTarget.path("backlogs");
        if (areaFilter != null)
            target = target.queryParam("area", areaFilter);
        if (projectFilter != null)
            target = target.queryParam("project", projectFilter);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    public URI createDayTask(String name, LocalDate date) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTask(String name, LocalDate date, boolean prioritized) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("prioritized", prioritized)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTask(String name, LocalDate date, Task assignedFrom) {
        return createFromAssigned(name, date, assignedFrom._self);
    }

    public URI createDayTask(String name, LocalDate date, BacklogItem assignedFrom) {
        return createFromAssigned(name, date, assignedFrom._self);
    }

    private URI createFromAssigned(String name, LocalDate date, URI assignedFrom) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("assignedFrom", assignedFrom.toString())
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskWithNote(String name, LocalDate date, String note) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("note", note)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskWithArea(String name, LocalDate date, String area) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("area", area)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskWithArea(String name, LocalDate date, int planned, String area, Task assignedFrom, boolean prioritized) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("planned", planned)
                .add("prioritized", prioritized)
                .add("area", area)
                .add("assignedFrom", assignedFrom._self.toString())
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskWithProject(String name, LocalDate date, int planned, String project) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("planned", planned)
                .add("project", project)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskWithProject(String name, LocalDate date, int planned, String project, Task assignedFrom, boolean prioritized) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("planned", planned)
                .add("prioritized", prioritized)
                .add("project", project)
                .add("assignedFrom", assignedFrom._self.toString())
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public URI createDayTaskRepeat(String name, LocalDate date, int repeat) {
        return createDayTaskRepeat(name, date, repeat, 1);
    }

    public URI createDayTaskRepeat(String name, LocalDate date, int repeat, int repeatCadenceDays) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("repeat", repeat)
                .add("repeatCadence", repeatCadenceDays)
                .build();

        return requestCreateVerify(taskEntity, date.toString(), "tasks");
    }

    public String updateTask(Task task, Object... values) {
        JsonObject patch = patch(values);

        Response response = requestUpdate(task._self, patch);
        verifySuccess(response);
        return getActionId(response);
    }

    public String addRelation(Task task, URI other) {
        JsonObject patch = patch("op", "add", "path", "/relations", "value", other);

        Response response = requestUpdate(task._self, patch, MediaType.APPLICATION_JSON_PATCH_JSON_TYPE);
        verifySuccess(response);
        return getActionId(response);
    }

    public String deleteTask(Task task) {
        Response response = requestDelete(task._self);
        verifySuccess(response);
        return getActionId(response);
    }

    public URI createWeekTask(String string, YearWeek week) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", string)
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createWeekTask(String string, YearWeek week, BacklogItem backlogItem) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", string)
                .add("assignedFrom", backlogItem._self.toString())
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createWeekTaskWithArea(String string, YearWeek week, int planned, String area) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", string)
                .add("planned", planned)
                .add("area", area)
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createWeekTaskWithProject(String string, YearWeek week, int planned, String project) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", string)
                .add("planned", planned)
                .add("project", project)
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createWeekTaskWithNote(String string, YearWeek week, String note) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", string)
                .add("note", note)
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createWeekTaskRepeat(String name, YearWeek week, int repeat) {
        return createWeekTaskRepeat(name, week, repeat, 1);
    }

    public URI createWeekTaskRepeat(String name, YearWeek week, int repeat, int repeatCadenceDays) {
        JsonObject taskEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("repeat", repeat)
                .add("repeatCadence", repeatCadenceDays)
                .build();

        return requestCreateVerify(taskEntity, week.toString(), "tasks");
    }

    public URI createDayTimeEvent(String name, LocalDateTime start, LocalDateTime end) {
        return createDayTimeEvent(name, start.toLocalDate(), start, end);
    }

    public URI createDayTimeEvent(String name, ZonedDateTime start, ZonedDateTime end) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .build();

        return requestCreateVerify(entity, start.toLocalDate().toString(), "day-time-events");
    }

    // for testing invalid values
    public URI createDayTimeEvent(String name, LocalDate date, LocalDateTime start, LocalDateTime end) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .build();

        return requestCreateVerify(entity, date.toString(), "day-time-events");
    }

    // for testing invalid values
    public URI createDayTimeEvent(String name, LocalDate date, String start, String end) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start)
                .add("end", end)
                .build();

        return requestCreateVerify(entity, date.toString(), "day-time-events");
    }

    public URI createDayTimeEvent(String name, LocalDateTime start, LocalDateTime end, Task assignedFrom) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .add("assignedFrom", assignedFrom._self.toString())
                .build();

        return requestCreateVerify(entity, start.toLocalDate().toString(), "day-time-events");
    }

    public URI createDayTimeEventWithNote(String name, LocalDateTime start, LocalDateTime end, String note) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .add("note", note)
                .build();

        return requestCreateVerify(entity, start.toLocalDate().toString(), "day-time-events");
    }

    public URI createDayTimeEventWithArea(String name, LocalDateTime start, LocalDateTime end, String area) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .add("area", area)
                .build();

        return requestCreateVerify(entity, start.toLocalDate().toString(), "day-time-events");
    }

    public URI createDayTimeEventWithProject(String name, LocalDateTime start, LocalDateTime end, String project) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .add("project", project)
                .build();

        return requestCreateVerify(entity, start.toLocalDate().toString(), "day-time-events");
    }

    public String updateDayTimeEvent(DayTimeEvent event, Object... values) {
        JsonObject patch = patch(values);
        Response response = requestUpdate(event._self, patch);
        verifySuccess(response);
        return getActionId(response);
    }

    public String deleteDayTimeEvent(DayTimeEvent event) {
        Response response = requestDelete(event._self);
        verifySuccess(response);
        return getActionId(response);
    }

    public String deleteDayTimeEvent(DayTimeEvent event, String recurring) {
        Response response = requestDelete(event._self, recurring);
        verifySuccess(response);
        return getActionId(response);
    }

    public String deleteDayEvent(DayEvent event) {
        Response response = requestDelete(event._self);
        verifySuccess(response);
        return getActionId(response);
    }

    private String getActionId(Response response) {
        String actionId = response.getHeaderString("Action-Id");
        if (actionId == null) throw new AssertionError("Action-Id header expected but wasn't present");
        return actionId;
    }

    // for testing invalid values
    public URI createDayEvent(String name, LocalDate date, LocalDate start, LocalDate end) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .build();

        return requestCreateVerify(entity, date.toString(), "day-events");
    }

    public URI createDayEvent(String name, LocalDate start, LocalDate end) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", name)
                .add("start", start.toString())
                .add("end", end.toString())
                .build();

        return requestCreateVerify(entity, start.toString(), "day-events");
    }

    public String updateDayEvent(DayEvent event, Object... values) {
        JsonObject patch = patch(values);
        Response response = requestUpdate(event._self, patch);
        verifySuccess(response);
        return getActionId(response);
    }

    public void updateDayNote(LocalDate date, String note) {
        updateNote(date.toString(), note);
    }

    public void updateWeekNote(YearWeek yearWeek, String note) {
        updateNote(yearWeek.toString(), note);
    }

    private void updateNote(String path, String note) {
        Invocation.Builder builder = rootTarget.path(path).path("note").request();
        Response response = (note != null)
                ? builder.put(Entity.text(note))
                : builder.delete();
        verifySuccess(response);
    }

    public URI createBacklog(String name) {
        JsonObject entity = Json.createObjectBuilder()
                .add("name", name)
                .build();

        return requestCreateVerify(entity, "backlogs");
    }

    public URI createBacklogWithArea(String name, String area) {
        JsonObject entity = Json.createObjectBuilder()
                .add("name", name)
                .add("area", area)
                .build();

        return requestCreateVerify(entity, "backlogs");
    }

    public URI createBacklogWithProject(String name, String project) {
        JsonObject entity = Json.createObjectBuilder()
                .add("name", name)
                .add("project", project)
                .build();

        return requestCreateVerify(entity, "backlogs");
    }

    public void updateBacklog(Backlog backlog, Object... values) {
        JsonObject patch = patch(values);
        Response response = requestUpdate(backlog._self, patch);
        verifySuccess(response);
    }

    public void deleteBacklog(Backlog backlog) {
        Response response = requestDelete(backlog._self);
        verifySuccess(response);
    }

    public URI createInboxItem(String name) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createInboxItemWithArea(String name, String area) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("area", area)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createInboxItemWithProject(String name, String project) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("project", project)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createInboxItemWithNote(String name, String note) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("note", note)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createBacklogItem(String name, Backlog backlog) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("backlog", backlog._self.toString())
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createBacklogItemWithArea(String name, Backlog backlog, String area) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("backlog", backlog._self.toString())
                .add("area", area)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createBacklogItemWithProject(String name, Backlog backlog, String project) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("backlog", backlog._self.toString())
                .add("project", project)
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public URI createBacklogItemWithNote(String name, String note, Backlog backlog) {
        JsonObject itemEntity = Json.createObjectBuilder()
                .add("string", name)
                .add("note", note)
                .add("backlog", backlog._self.toString())
                .build();

        return requestCreateVerify(itemEntity, "backlog-items");
    }

    public String updateBacklogItem(BacklogItem backlogItem, Object... values) {
        JsonObject patch = patch(values);
        Response response = requestUpdate(backlogItem._self, patch);
        verifySuccess(response);
        return getActionId(response);
    }

    public String deleteBacklogItem(BacklogItem backlogItem) {
        Response response = requestDelete(backlogItem._self);
        verifySuccess(response);
        return getActionId(response);
    }

    public URI createProject(String string) {
        JsonObject json = Json.createObjectBuilder()
                .add("string", string)
                .build();

        return requestCreateVerify(json, "projects");
    }

    public URI createProjectWithArea(String string, String area) {
        JsonObject json = Json.createObjectBuilder()
                .add("string", string)
                .add("area", area)
                .build();

        return requestCreateVerify(json, "projects");
    }

    public URI createProjectWithNote(String string, String note) {
        JsonObject json = Json.createObjectBuilder()
                .add("string", string)
                .add("note", note)
                .build();

        return requestCreateVerify(json, "projects");
    }

    public void updateProject(Project project, Object... values) {
        JsonObject patch = patch(values);
        Response response = requestUpdate(project._self, patch);
        verifySuccess(response);
    }

    public Project getProject(URI uri) {
        Response response = request(uri);
        verifySuccess(response);
        return response.readEntity(Project.class);
    }

    public List<Project> getProjects(boolean archived) {
        WebTarget target = rootTarget.path("projects");
        if (archived)
            target = target.queryParam("archived", "true");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).get();
        verifySuccess(response);
        GenericType<List<Project>> entityType = new GenericType<>() {
        };
        return response.readEntity(entityType);
    }

    public void deleteProject(Project project) {
        Response response = requestDelete(project._self);
        verifySuccess(response);
    }

    public void deleteProjects(String... names) {
        Set<String> strings = Set.of(names);
        getProjects(true).stream()
                .filter(p -> strings.contains(p.string))
                .forEach(this::deleteProject);
    }

    public Detection detect(String string) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", string)
                .build();
        return requestDetection(entity);
    }

    public Detection detectWithTimes(String string) {
        JsonObject entity = Json.createObjectBuilder()
                .add("string", string)
                .add("times", true)
                .build();
        return requestDetection(entity);
    }

    private Detection requestDetection(JsonObject entity) {
        Response response = requestCreate(entity, "detections");
        verifySuccess(response);
        return response.readEntity(Detection.class);
    }

    private URI requestCreateVerify(JsonObject taskEntity, String... paths) {
        Response response = requestCreate(taskEntity, paths);
        verifySuccess(response);
        return response.getLocation();
    }

    private JsonObject patch(String property, Object value) {
        JsonObjectBuilder patch = Json.createObjectBuilder();
        addPatchValue(patch, property, value);
        return patch.build();
    }

    private JsonObject patch(Object... values) {
        if (values.length % 2 != 0)
            throw new IllegalArgumentException("Length of the arguments have to be even");

        JsonObjectBuilder patch = Json.createObjectBuilder();
        for (int i = 0; i < values.length; i += 2)
            addPatchValue(patch, (String) values[i], values[i + 1]);
        return patch.build();
    }

    private void addPatchValue(JsonObjectBuilder patch, String key, Object value) {
        if (value == null)
            patch.addNull(key);
        else if (value instanceof Integer)
            patch.add(key, (int) value);
        else if (value instanceof Boolean)
            patch.add(key, (boolean) value);
        else
            patch.add(key, Objects.toString(value));
    }

    public void deleteAreas(String... names) {
        Set<String> strings = Set.of(names);
        getAreas().stream()
                .filter(b -> strings.contains(b.name))
                .forEach(this::deleteArea);
    }

    public void deleteDayTasks(LocalDate date) {
        getDay(date).tasks.forEach(this::deleteTask);
    }

    public void deleteWeekTasks(YearWeek week) {
        getWeek(week).tasks.forEach(this::deleteTask);
    }

    public void deleteDayTimeEvents(LocalDate... dates) {
        Stream.of(dates).forEach(date -> getDay(date).timeEvents.forEach(this::deleteDayTimeEvent));
    }

    public void deleteDayEvents(LocalDate... dates) {
        Stream.of(dates).forEach(date -> getDay(date).dayEvents.forEach(this::deleteDayEvent));
    }

    public void deleteBacklogs(String name) {
        getBacklogs().stream()
                .filter(b -> b.name.equals(name))
                .forEach(this::deleteBacklog);
    }

    public void deleteBacklogItemsInAllBacklogs(String... names) {
        Set<String> strings = Set.of(names);
        getBacklogs().stream()
                .map(b -> getBacklog(b._self, true))
                .map(b -> b.items)
                .flatMap(Collection::stream)
                .filter(i -> strings.contains(i.string))
                .forEach(this::deleteBacklogItem);
    }

    private Response requestCreate(JsonObject entity, String... paths) {
        WebTarget target = rootTarget;
        for (String path : paths) {
            target = target.path(path);
        }
        return target.request().post(Entity.json(entity));
    }

    private Response requestUpdate(URI uri, JsonObject patch) {
        return client.target(uri)
                .request()
                .method("PATCH", Entity.json(patch));
    }

    private Response requestUpdate(URI uri, JsonObject patch, MediaType mediaType) {
        return client.target(uri)
                .request()
                .method("PATCH", Entity.entity(patch, mediaType));
    }

    private Response requestDelete(URI uri) {
        return client.target(uri)
                .request()
                .delete();
    }

    private Response requestDelete(URI uri, String recurring) {
        return client.target(uri)
                .queryParam("recurring", recurring)
                .request()
                .delete();
    }

    private void verifySuccess(Response response) {
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            System.err.println(response.getHeaders());
            throw new AssertionError("Status was not successful: " + response.getStatus());
        }
    }

    public SearchResult search(String query) {
        JsonObject search = Json.createObjectBuilder()
                .add("query", query)
                .build();

        return requestSearchVerify(search, false);
    }

    public SearchResult searchAdvanced(String query, String area, String project) {
        JsonObjectBuilder builder = Json.createObjectBuilder().add("query", query);
        if (project != null)
            builder.add("project", project);
        else if (area != null)
            builder.add("area", area);
        return requestSearchVerify(builder.build(), true);
    }

    public SearchResult searchAdvanced(String query, String area, String project, LocalDate fromDate, LocalDate toDate) {
        JsonObjectBuilder builder = Json.createObjectBuilder().add("query", query);
        if (project != null)
            builder.add("project", project);
        else if (area != null)
            builder.add("area", area);
        if (fromDate != null)
            builder.add("from", fromDate.toString());
        if (toDate != null)
            builder.add("to", toDate.toString());
        return requestSearchVerify(builder.build(), true);
    }

    public SearchResult searchPotentialRelations(URI uri, String query) {
        JsonObject search = Json.createObjectBuilder()
                .add("query", query)
                .add("relation", uri.toString())
                .build();

        return requestSearchVerify(search, false);
    }

    private SearchResult requestSearchVerify(JsonObject search, boolean fullInformation) {
        WebTarget target = rootTarget.path("searches");
        if (fullInformation)
            target = target.queryParam("showFullInformation", "true");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(search));

        verifySuccess(response);
        return response.readEntity(SearchResult.class);
    }

    public void undo(String actionId) {
        Response response = rootTarget.path("actions/undo")
                .request()
                .post(Entity.json(Json.createObjectBuilder().add("actionId", actionId).build()));
        verifySuccess(response);
    }

    public void registerCountUpdates() {
        updateCounter = 0;
        registerForUpdates(() -> updateCounter++);
    }

    public int getRegisteredUpdates() {
        return updateCounter;
    }

    public void registerForUpdates(Runnable onUpdate) {
        registerForUpdates(ev -> {
            if (".".equals(ev.readData()))
                onUpdate.run();
        });
    }

    public void registerForUpdates(Runnable onUpdate, Consumer<String> onMessage) {
        registerForUpdates(ev -> {
            String data = ev.readData();
            if (".".equals(data))
                onUpdate.run();
            else
                onMessage.accept(data);
        });
    }

    private void registerForUpdates(Consumer<InboundSseEvent> eventConsumer) {
        if (updateSource == null) {
            updateSource = SseEventSource.target(rootTarget.path("user/updates"))
                    .build();
        }
        updateSource.register(eventConsumer, thr -> {
            System.err.println("Error in SSE updates");
            throw new RuntimeException(thr);
        });

        updateSource.open();
    }

    public void close() {
        if (updateSource != null)
            updateSource.close();
    }

}
