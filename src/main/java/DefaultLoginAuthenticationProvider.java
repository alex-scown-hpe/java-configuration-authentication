/*
 * Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

import com.hp.autonomy.frontend.configuration.AuthenticationConfig;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.LoginTypes;
import com.hp.autonomy.frontend.configuration.UsernameAndPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DefaultLoginAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ConfigService<? extends AuthenticationConfig<?>> configService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final com.hp.autonomy.frontend.configuration.Authentication<?> authenticationConfig = configService.getConfig().getAuthentication();

        if(!LoginTypes.DEFAULT.equalsIgnoreCase(authenticationConfig.getMethod())) {
            return null;
        }

        final UsernameAndPassword defaultLogin = authenticationConfig.getDefaultLogin();

        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        if(defaultLogin.getUsername().equals(username) && defaultLogin.getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(new SimpleGrantedAuthority("ROLE_DEFAULT")));
        }
        else {
            throw new BadCredentialsException("Access is denied");
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class == authentication;
    }
}
