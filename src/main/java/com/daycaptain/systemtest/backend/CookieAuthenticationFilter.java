package com.daycaptain.systemtest.backend;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
public class CookieAuthenticationFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext context) {
        context.getHeaders().add(HttpHeaders.COOKIE, new Cookie("q_session", CookieAuthentication.SESSION_COOKIE));
    }

}
