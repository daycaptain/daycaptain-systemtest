package com.daycaptain.systemtest.backend.backlogs;

import com.daycaptain.systemtest.backend.DayCaptainSystem;
import com.daycaptain.systemtest.backend.entity.Backlog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListBacklogsTest {

    private final DayCaptainSystem dayCaptain = new DayCaptainSystem();

    @AfterEach
    void tearDown() {
        dayCaptain.deleteBacklogs("First");
        dayCaptain.deleteBacklogs("Second");
        dayCaptain.deleteBacklogs("Third");
    }

    @Test
    void testListBacklogs() {
        URI first = dayCaptain.createBacklog("First");
        URI second = dayCaptain.createBacklog("Second");
        URI third = dayCaptain.createBacklog("Third");

        List<Backlog> backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(b -> b._self).containsSequence(first, second, third);
    }

    @Test
    void testListBacklogsFilterArea() {
        URI first = dayCaptain.createBacklog("First");
        URI second = dayCaptain.createBacklogWithArea("Second", "IT work");
        URI third = dayCaptain.createBacklog("Third");

        List<Backlog> backlogs = dayCaptain.getBacklogs("IT work");
        assertThat(backlogs).extracting(b -> b._self).contains(second);
        assertThat(backlogs).extracting(b -> b._self).doesNotContain(first, third);
    }

    @Test
    void testResort() {
        List<Backlog> backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "To-contact", "IT work", "Business idea");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // middle to top
        dayCaptain.updateBacklog(backlogs.get(2), "priority", 1);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "IT work", "To-contact", "Business idea");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // middle to end
        dayCaptain.updateBacklog(backlogs.get(2), "priority", 3);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "IT work", "Business idea", "To-contact");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // end to top
        dayCaptain.updateBacklog(backlogs.get(3), "priority", 1);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "To-contact", "IT work", "Business idea");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // end to middle
        dayCaptain.updateBacklog(backlogs.get(3), "priority", 2);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "To-contact", "Business idea", "IT work");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // top to end
        dayCaptain.updateBacklog(backlogs.get(1), "priority", 3);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "Business idea", "IT work", "To-contact");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // top to middle
        dayCaptain.updateBacklog(backlogs.get(1), "priority", 2);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "IT work", "Business idea", "To-contact");
        assertThat(backlogs).extracting(t -> t.priority).containsExactly(0, 1, 2, 3);

        // restore original sorting
        dayCaptain.updateBacklog(backlogs.get(3), "priority", 1);
        backlogs = dayCaptain.getBacklogs();
        assertThat(backlogs).extracting(t -> t.name).containsExactly("INBOX", "To-contact", "IT work", "Business idea");
    }

}
