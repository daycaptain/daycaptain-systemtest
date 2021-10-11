package com.daycaptain.systemtest.frontend.scenarios;

import com.daycaptain.systemtest.frontend.actions.EditInformationAction;
import com.daycaptain.systemtest.frontend.elements.ListElement;
import org.junit.jupiter.api.DynamicTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class EditListItemScenario {

    private static final String area = "IT work";
    private static final String areaKeyCode = "I";
    private static final String projectWithArea = "Business idea";
    private static final String projectsArea = "Business";
    private static final String projectWithoutArea = "Spanish";

    private final String namePrefix;
    private final Runnable preRun;
    private final Supplier<ListElement> elementSupplier;

    public EditListItemScenario(String namePrefix, Runnable preRun, Supplier<ListElement> elementSupplier) {
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

    void name() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setName("Edited item name");
        editAction.save();

        assertThat(listElement.getList().get(0).string).isEqualTo("Edited item name");
        assertThat(listElement.getList().get(0).project).isNull();
        assertThat(listElement.getList().get(0).hasArea).isFalse();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    void abort() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setName("Edited item name");
        editAction.close();

        assertThat(listElement.getList().get(0).string).isEqualTo("New item");
        assertThat(listElement.getList().get(0).project).isNull();
        assertThat(listElement.getList().get(0).hasArea).isFalse();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    void area() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setArea(areaKeyCode);
        editAction.save();

        assertThat(listElement.getList().get(0).string).isEqualTo("New item");
        assertThat(listElement.getList().get(0).project).isNull();
        assertThat(listElement.getList().get(0).hasArea).isTrue();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo(area);
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    void projectWithArea() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setProject(projectWithArea);
        editAction.save();

        assertThat(listElement.getList().get(0).string).isEqualTo("New item");
        assertThat(listElement.getList().get(0).project).isEqualTo(projectWithArea);
        assertThat(listElement.getList().get(0).hasArea).isTrue();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo(projectWithArea);
        assertThat(retrieveAction.getArea()).isEqualTo(projectsArea);
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    void projectWithoutArea() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setProject(projectWithoutArea);
        editAction.save();

        assertThat(listElement.getList().get(0).string).isEqualTo("New item");
        assertThat(listElement.getList().get(0).project).isEqualTo(projectWithoutArea);
        assertThat(listElement.getList().get(0).hasArea).isFalse();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo(projectWithoutArea);
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getNote()).isEmpty();
        retrieveAction.close();
    }

    void note() {
        ListElement listElement = elementSupplier.get();
        listElement.createSave("New item");
        EditInformationAction editAction = listElement.edit(0);
        editAction.setNote("This is a note");
        editAction.save();

        assertThat(listElement.getList().get(0).string).isEqualTo("New item");
        assertThat(listElement.getList().get(0).project).isNull();
        assertThat(listElement.getList().get(0).hasArea).isFalse();

        EditInformationAction retrieveAction = listElement.edit(0);
        assertThat(retrieveAction.getProject()).isEqualTo("No project");
        assertThat(retrieveAction.getArea()).isEqualTo("No area");
        assertThat(retrieveAction.getNote()).isEqualTo("This is a note");
        retrieveAction.close();
    }

}
