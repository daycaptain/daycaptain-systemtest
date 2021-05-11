package com.daycaptain.systemtest.backend.areas;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Area;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAreaTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteAreas("New area");
        dayCaptain.deleteAreas("New area 2");
    }

    @Test
    void testCreateArea() {
        int size = dayCaptain.getAreas().size();
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area._self).isEqualTo(itemId);
        assertThat(area.name).isEqualTo("New area");
        assertThat(area.order).isEqualTo(size + 1);
        assertThat(area.color).isEqualTo("#1e90ff");
        assertThat(area.keyCode).isEqualTo("N");
    }

    @Test
    void testCreateAreaAlternativeKeyCodeAndColor() {
        int size = dayCaptain.getAreas().size();
        URI itemId = dayCaptain.createArea("New area");
        assertThat(itemId).isNotNull();

        Area area = dayCaptain.getArea(itemId);
        assertThat(area._self).isEqualTo(itemId);
        assertThat(area.name).isEqualTo("New area");
        assertThat(area.order).isEqualTo(size + 1);
        assertThat(area.color).isEqualTo("#1e90ff");
        assertThat(area.keyCode).isEqualTo("N");

        itemId = dayCaptain.createArea("New area 2");
        assertThat(itemId).isNotNull();

        area = dayCaptain.getArea(itemId);
        assertThat(area._self).isEqualTo(itemId);
        assertThat(area.name).isEqualTo("New area 2");
        assertThat(area.order).isEqualTo(size + 2);
        assertThat(area.color).isEqualTo("#ffff00");
        assertThat(area.keyCode).isEqualTo("E");
    }

}