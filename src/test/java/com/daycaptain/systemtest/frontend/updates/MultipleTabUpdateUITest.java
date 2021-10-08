package com.daycaptain.systemtest.frontend.updates;

import com.codeborne.selenide.SelenideElement;
import com.daycaptain.systemtest.frontend.DayCaptainUI;
import com.daycaptain.systemtest.frontend.views.DayView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static com.daycaptain.systemtest.frontend.views.View.waitFor;
import static org.assertj.core.api.Assertions.assertThat;

public class MultipleTabUpdateUITest {

    private final DayCaptainUI dayCaptain = new DayCaptainUI();

    @BeforeEach
    void setUp() {
        dayCaptain.initWithLogin();
    }

    @Test
    void change_should_be_updated_in_multiple_tabs() {
        DayView day = dayCaptain.day();

        // close potential messages
        press(Keys.ESCAPE);

        // hack to open a new tab
        press(".");
        SelenideElement a = $("main-navigation ul li a");
        a.toWebElement().sendKeys(Keys.chord(Keys.CONTROL, Keys.ENTER));

        // will be in backlogs view
        switchTo().window(1);
        dayCaptain.day();
        press(Keys.ESCAPE);

        // need to refresh to activate service workers
        refresh();

        switchTo().window(0);
        refresh();
        dayCaptain.day().tasks().create("Hello");
        int size = day.tasks().getList().size();

        switchTo().window(1);
        waitFor(200);
        assertThat(day.tasks().getList()).hasSize(size);
    }

}
