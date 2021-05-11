package com.daycaptain.systemtest.frontend.scenarios;

import com.daycaptain.systemtest.frontend.actions.CreateAction;
import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.elements.ListElement;
import com.daycaptain.systemtest.frontend.entity.ListItem;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateListItemScenario {

    private static final String area = "IT work";
    private static final String areaKeyCode = "I";
    private static final String projectWithArea = "Business idea";
    private static final String projectsArea = "Business";
    private static final String projectWithoutArea = "Spanish";

    private final String namePrefix;
    private final Runnable preRun;
    private final Supplier<ListElement> elementSupplier;

    public CreateListItemScenario(String namePrefix, Runnable preRun, Supplier<ListElement> elementSupplier) {
        this.namePrefix = namePrefix;
        this.preRun = preRun;
        this.elementSupplier = elementSupplier;
    }

    public Collection<DynamicTest> testCases() {
        return Arrays.stream(getClass().getDeclaredMethods())
                .filter(m -> !m.getName().contains("$") && m.getReturnType().equals(void.class))
                .map(m -> DynamicTest.dynamicTest(namePrefix + "_" + m.getName(), () -> {
                    preRun.run();
                    m.invoke(this);
                }))
                .collect(Collectors.toList());
    }

    void withName() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.save();

        ListItem item = listElement.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();

        EditInformationAction editTaskAction = listElement.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        assertThat(editTaskAction.getProject()).isEqualTo("No project");
        editTaskAction.close();
    }

    void abort() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.close();
        assertThat(listElement.getList()).isEmpty();
    }

    void withArea() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.setArea(areaKeyCode);
        createAction.save();

        ListItem item = listElement.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isTrue();
        assertThat(item.project).isNull();
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();

        EditInformationAction editTaskAction = listElement.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo(area);
        assertThat(editTaskAction.getProject()).isEqualTo("No project");
        editTaskAction.close();
    }

    void withProjectWithoutArea() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.setProject(projectWithoutArea);
        createAction.save();

        ListItem item = listElement.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isEqualTo(projectWithoutArea);
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();

        EditInformationAction editTaskAction = listElement.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        assertThat(editTaskAction.getProject()).isEqualTo(projectWithoutArea);
        editTaskAction.close();
    }

    void withProjectWithArea() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.setProject(projectWithArea);
        createAction.save();

        ListItem item = listElement.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isTrue();
        assertThat(item.project).isEqualTo(projectWithArea);
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();

        EditInformationAction editTaskAction = listElement.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo(projectsArea);
        assertThat(editTaskAction.getProject()).isEqualTo(projectWithArea);
        editTaskAction.close();
    }

    void projectIsDominantOverArea() {
        ListElement listElement = elementSupplier.get();
        CreateAction createAction = listElement.create();
        createAction.setName("New item");
        createAction.setArea(area);
        createAction.setProject(projectWithoutArea);
        createAction.save();

        ListItem item = listElement.getList().get(0);
        assertThat(item.string).isEqualTo("New item");
        assertThat(item.hasArea).isFalse();
        assertThat(item.project).isEqualTo(projectWithoutArea);
        assertThat(item.hasRelation).isFalse();
        assertThat(item.hasNote).isFalse();

        EditInformationAction editTaskAction = listElement.edit(0);
        assertThat(editTaskAction.getName()).isEqualTo("New item");
        assertThat(editTaskAction.getArea()).isEqualTo("No area");
        assertThat(editTaskAction.getProject()).isEqualTo(projectWithoutArea);
        assertThat(editTaskAction.getNote()).isEmpty();
        editTaskAction.close();
    }

}
