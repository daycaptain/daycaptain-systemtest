package com.daycaptain.systemtest.backend.detection;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Detection;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DetectionTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @Test
    void project_is_detected() {
        Detection detection = dayCaptain.detect("business");
        assertThat(detection.project).isEqualTo("Business idea");
        assertThat(detection.area).isNull();
        System.out.println("detection.eventStart = " + detection.eventStart);
        System.out.println("detection.eventEnd = " + detection.eventEnd);
    }

    @Test
    void unknown_name_not_detected() {
        Detection detection = dayCaptain.detect("biz");
        assertThat(detection.project).isNull();
        assertThat(detection.area).isNull();
        System.out.println("detection.eventStart = " + detection.eventStart);
        System.out.println("detection.eventEnd = " + detection.eventEnd);
    }

    @Test
    void area_is_detected() {
        Detection detection = dayCaptain.detect("work");
        assertThat(detection.project).isNull();
        assertThat(detection.area).isEqualTo("IT work");
        System.out.println("detection.eventStart = " + detection.eventStart);
        System.out.println("detection.eventEnd = " + detection.eventEnd);
    }

    @Test
    void archived_area_not_detected() {
        Detection detection = dayCaptain.detect("old");
        assertThat(detection.project).isNull();
        assertThat(detection.area).isNull();
        System.out.println("detection.eventStart = " + detection.eventStart);
        System.out.println("detection.eventEnd = " + detection.eventEnd);
    }

    @Test
    void archived_project_not_detected() {
        Detection detection = dayCaptain.detect("past");
        assertThat(detection.project).isNull();
        assertThat(detection.area).isNull();
        System.out.println("detection.eventStart = " + detection.eventStart);
        System.out.println("detection.eventEnd = " + detection.eventEnd);
    }

    @Test
    void times_are_detected() {
        Detection detection = dayCaptain.detectWithTimes("project");
        assertThat(detection.eventStart).isEqualTo("08:00:00");
        assertThat(detection.eventEnd).isEqualTo("11:00:00");
        assertThat(detection.project).isEqualTo("Business idea");
        assertThat(detection.area).isNull();

        detection = dayCaptain.detectWithTimes("reading");
        assertThat(detection.eventStart).isEqualTo("15:00:00");
        assertThat(detection.eventEnd).isEqualTo("16:15:00");
        assertThat(detection.project).isNull();
        assertThat(detection.area).isNull();
    }

}
