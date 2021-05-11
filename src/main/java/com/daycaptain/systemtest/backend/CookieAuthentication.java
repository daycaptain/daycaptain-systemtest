package com.daycaptain.systemtest.backend;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CookieAuthentication {

    // cookie literal has been replaced by OS shell script that emits local cookie
    // see example under ./tools/_dc-cookie
    public static String SESSION_COOKIE;

    static {
        try {
            Process process = new ProcessBuilder("_dc-cookie", "test").start();
            process.waitFor(1, TimeUnit.SECONDS);
            SESSION_COOKIE = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            System.err.println("Could not execute _dc-cookie test");
            e.printStackTrace();
        }
    }
}
