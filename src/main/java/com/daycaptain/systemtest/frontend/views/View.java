package com.daycaptain.systemtest.frontend.views;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import static com.codeborne.selenide.Selenide.actions;

public abstract class View {

    public static void press(CharSequence... key) {
        actions().sendKeys(key).perform();
    }

    public static void shiftPress(CharSequence... key) {
        actions()
                .keyDown(Keys.SHIFT)
                .sendKeys(key)
                .keyUp(Keys.SHIFT)
                .perform();
    }

    public static void altPress(CharSequence... key) {
        actions()
                .keyDown(Keys.ALT)
                .sendKeys(key)
                .keyUp(Keys.ALT)
                .perform();
    }

    public static void ctrlPress(CharSequence... key) {
        actions()
                .keyDown(Keys.CONTROL)
                .sendKeys(key)
                .keyUp(Keys.CONTROL)
                .perform();
    }

    public static Actions key() {
        return actions();
    }

    public static void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
