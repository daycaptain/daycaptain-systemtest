package com.daycaptain.systemtest.frontend.actions;

import com.codeborne.selenide.SelenideElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.daycaptain.systemtest.frontend.views.View.ctrlPress;
import static com.daycaptain.systemtest.frontend.views.View.press;
import static org.openqa.selenium.Keys.ENTER;

public class SearchAction extends Action {

    public SearchAction() {
        super();
        waitForLoading();
    }

    public void searchTerm(String searchTerm) {
        SelenideElement input = $("input.search");
        input.clear();
        input.sendKeys(searchTerm);
        waitForLoading();
    }

    public void gotoSelection() {
        press(ENTER);
    }

    public void gotoSelection(int index) {
        for (int i = 0; i < index; i++)
            ctrlPress("j");
        press(ENTER);
    }

    public List<String> getResults() {
        return $$("result-list result-item").stream()
                .map(el -> el.getOwnText().trim())
                .collect(Collectors.toList());
    }

    private void waitForLoading() {
        $("div.overlay search-action").shouldBe(visible);
        $("div.loading").shouldNotBe(visible);
    }

}
