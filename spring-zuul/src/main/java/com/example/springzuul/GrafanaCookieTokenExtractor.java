package com.example.springzuul;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Component
public class GrafanaCookieTokenExtractor extends BearerTokenExtractor {

    private final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @Override
    public Authentication extract(HttpServletRequest request) {
        Authentication authentication = super.extract(request);
        if (authentication != null) {
            return authentication;
        }


        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);

        return Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .map(token -> new PreAuthenticatedAuthenticationToken(token, ""))
                .orElse(null);
    }
}
