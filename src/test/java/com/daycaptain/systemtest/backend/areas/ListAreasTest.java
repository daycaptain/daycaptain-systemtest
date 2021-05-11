package com.daycaptain.systemtest.backend.areas;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Area;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListAreasTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void testListAreas() {
        List<Area> backlogs = dayCaptain.getAreas();
        assertThat(backlogs).extracting(b -> b.name).containsSequence("IT work", "Self-improvement", "Business");
        assertThat(backlogs).isSortedAccordingTo(Comparator.comparing(a -> a.order));
    }

    @Test
    void testResort() {
        List<Area> areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("IT work", "Self-improvement", "Business");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // middle to top
        dayCaptain.updateArea(areas.get(1), "order", 1);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("Self-improvement", "IT work", "Business");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // middle to end
        dayCaptain.updateArea(areas.get(1), "order", 3);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("Self-improvement", "Business", "IT work");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // end to top
        dayCaptain.updateArea(areas.get(2), "order", 1);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("IT work", "Self-improvement", "Business");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // end to middle
        dayCaptain.updateArea(areas.get(2), "order", 2);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("IT work", "Business", "Self-improvement");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // top to end
        dayCaptain.updateArea(areas.get(0), "order", 3);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("Business", "Self-improvement", "IT work");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // top to middle
        dayCaptain.updateArea(areas.get(0), "order", 2);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("Self-improvement", "Business", "IT work");
        assertThat(areas).isSortedAccordingTo(Comparator.comparing(a -> a.order));

        // restore original
        dayCaptain.updateArea(areas.get(2), "order", 1);
        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("IT work", "Self-improvement", "Business");
    }

    @Test
    void testSortArchivedAtEnd() {
        URI itemId = dayCaptain.createArea("Area");
        Area area = dayCaptain.getArea(itemId);

        dayCaptain.updateArea(area, "order", 1);
        List<Area> areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("Area", "IT work", "Self-improvement", "Business");
        dayCaptain.updateArea(area, "archived", true);

        areas = dayCaptain.getAreas();
        assertThat(areas).extracting(t -> t.name).containsExactly("IT work", "Self-improvement", "Business", "Area");

        // restore original
        dayCaptain.deleteAreas("Area");
        areas = dayCaptain.getAreas();
        dayCaptain.updateArea(areas.get(0), "order", 1);
    }

}
