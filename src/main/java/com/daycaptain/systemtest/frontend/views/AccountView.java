package com.daycaptain.systemtest.frontend.views;

import static com.codeborne.selenide.Selenide.$;

public class AccountView extends DynamicView {

    public String getSubscriptionStatus() {
        String text = $("form section:nth-of-type(2) div:first-of-type > div:first-of-type").text();
        return text.replace("Subscription status: ", "");
    }

    public String getFirstName() {
        return $("input#firstname").val();
    }

}
