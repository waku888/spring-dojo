package com.example.blog.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.List;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JsonUsernamePasswordAuthenticationFilter(
            SecurityContextRepository securityContextRepository,
            SessionAuthenticationStrategy sessionAuthenticationStrategy
    ) {
        super();
        setSecurityContextRepository(securityContextRepository);
        setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        setAuthenticationSuccessHandler((req, res, auth) -> {
           res.setStatus(HttpServletResponse.SC_OK);
        });
        setAuthenticationFailureHandler((req, res, auth) -> {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        });
    }
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        return UsernamePasswordAuthenticationToken.authenticated(
                "dummy-user",
                "dummy-pass",
                List.of()
        );
    }
}
