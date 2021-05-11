package com.daycaptain.systemtest.frontend;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.daycaptain.systemtest.backend.CookieAuthentication;
import com.daycaptain.systemtest.frontend.views.*;
import org.openqa.selenium.Cookie;
import org.threeten.extra.YearWeek;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.daycaptain.systemtest.frontend.views.View.waitFor;
import static java.util.logging.Level.SEVERE;
import static org.openqa.selenium.logging.LogType.BROWSER;

public class DayCaptainUI {

    public DayCaptainUI() {
        Configuration.browser = "chrome";
        open();
    }

    public void initWithLogin() {
        open(uriBuilder().toString());
        WebDriverRunner.driver().getWebDriver().manage().addCookie(new Cookie("q_session", CookieAuthentication.SESSION_COOKIE));
    }

    private UriBuilder uriBuilder() {
        String port = System.getProperty("daycaptain.test.port", "8080");
        String host = System.getProperty("daycaptain.test.host", "localhost");
        return UriBuilder.fromUri("http://{host}:{port}/")
                .resolveTemplate("host", host)
                .resolveTemplate("port", port);
    }

    public DayView day() {
        open(uriBuilder().path("day.html").build().toString());
        return new DayView();
    }

    public DayView day(LocalDate date) {
        open(uriBuilder().path("day.html").fragment(date.toString()).build().toString());
        refresh();
        return new DayView();
    }

    public WeekView week() {
        open(uriBuilder().path("week.html").build().toString());
        return new WeekView();
    }

    public WeekView week(YearWeek week) {
        open(uriBuilder().path("week.html").fragment(week.toString()).build().toString());
        refresh();
        return new WeekView();
    }

    public BacklogsView backlogs() {
        open(uriBuilder().path("backlogs.html").build().toString());
        return new BacklogsView();
    }

    public AccountView account() {
        open(uriBuilder().path("account.html").build().toString());
        return new AccountView();
    }

    public String currentView() {
        return WebDriverRunner.url();
    }

    public DayView dayLink(URI link) {
        follow(link);
        return new DayView();
    }

    public WeekView weekLink(URI link) {
        follow(link);
        return new WeekView();
    }

    public BacklogsView backlogsLink(URI link) {
        follow(link);
        return new BacklogsView();
    }

    private void follow(URI link) {
        open(link.toString());
        waitFor(200);
    }

    public void close() {
        List<String> logs = Selenide.getWebDriverLogs(BROWSER, SEVERE);
        if (!logs.isEmpty()) {
            logs.forEach(System.err::println);
            throw new IllegalStateException("JS errors occurred!");
        }
        closeWindow();
    }

}
