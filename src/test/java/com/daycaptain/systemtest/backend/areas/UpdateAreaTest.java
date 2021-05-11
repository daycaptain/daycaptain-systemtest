package com.daycaptain.systemtest.backend.areas;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Area;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateAreaTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteAreas("New area");
        dayCaptain.deleteAreas("Another area");
    }

    @Test
    void testUpdateName() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.name).isEqualTo("New area");

        dayCaptain.updateArea(area, "name", "Another area");
        area = dayCaptain.getArea(itemId);
        assertThat(area.name).isEqualTo("Another area");
    }

    @Test
    void testInvalidCreateNameTaken() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.createArea("New area"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testInvalidUpdateNameTaken() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();
        dayCaptain.createArea("Another area");

        Area area = dayCaptain.getArea(itemId);
        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateArea(area, "name", "Another area"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testUpdateNameSameName() {
        URI itemId = dayCaptain.createArea("New area");

        Area area = dayCaptain.getArea(itemId);
        dayCaptain.updateArea(area, "name", "New area");

        area = dayCaptain.getArea(itemId);
        assertThat(area.name).isEqualTo("New area");
    }

    @Test
    void testUpdateKeyCode() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.keyCode).isEqualTo("N");

        dayCaptain.updateArea(area, "keyCode", "A");
        area = dayCaptain.getArea(itemId);
        assertThat(area.keyCode).isEqualTo("A");
    }

    @Test
    void testInvalidUpdateKeyCodeTaken() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        String keyCode = dayCaptain.getArea(itemId).keyCode;
        assertThat(keyCode).isEqualTo("N");

        itemId = dayCaptain.createArea("Another area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.keyCode).isEqualTo("A");

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateArea(area, "keyCode", "N"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testUpdateKeyCodeTakenSameArea() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.keyCode).isEqualTo("N");

        dayCaptain.updateArea(area, "keyCode", "N");
        area = dayCaptain.getArea(itemId);
        assertThat(area.keyCode).isEqualTo("N");
    }

    @Test
    void testUpdateColor() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.color).isEqualTo("#1e90ff");

        dayCaptain.updateArea(area, "color", "#ffff00");
        area = dayCaptain.getArea(itemId);
        assertThat(area.color).isEqualTo("#ffff00");
    }

    @Test
    void testInvalidUpdateColorTaken() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        String color = dayCaptain.getArea(itemId).color;
        assertThat(color).isEqualTo("#1e90ff");

        itemId = dayCaptain.createArea("Another area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.color).isEqualTo("#ffff00");

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> dayCaptain.updateArea(area, "color", "#1e90ff"));
        assertThat(error.getMessage()).isEqualTo("Status was not successful: 400");
    }

    @Test
    void testUpdateColorTakenSameArea() {
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area.color).isEqualTo("#1e90ff");

        dayCaptain.updateArea(area, "color", "#1e90ff");
        area = dayCaptain.getArea(itemId);
        assertThat(area.color).isEqualTo("#1e90ff");
    }

    @Test
    void testArchiveArea() {
        URI itemId = dayCaptain.createArea("New area");
        Area area = dayCaptain.getArea(itemId);
        assertThat(area.archived).isFalse();
        assertThat(area.keyCode).isEqualTo("N");

        dayCaptain.updateArea(area, "archived", true);
        area = dayCaptain.getArea(itemId);
        assertThat(area.archived).isTrue();
        assertThat(area.keyCode).isNull();
    }

    @Test
    void testRestoreArchivedArea() {
        URI itemId = dayCaptain.createArea("New area");
        Area area = dayCaptain.getArea(itemId);

        dayCaptain.updateArea(area, "archived", true);
        area = dayCaptain.getArea(itemId);
        assertThat(area.archived).isTrue();
        assertThat(area.keyCode).isNull();

        dayCaptain.updateArea(area, "archived", false);
        area = dayCaptain.getArea(itemId);
        assertThat(area.archived).isFalse();
        assertThat(area.keyCode).isEqualTo("N");
    }

}