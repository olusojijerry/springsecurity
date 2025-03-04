package com.jerryssec.springsecuritysetup.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomNonProdAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    /*Here you can write logic to check for other business process for authentication*/
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username  = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

//        if(passwordEncoder.matches(password, userDetails.getPassword()))
//            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
//        else
//            throw new BadCredentialsException("Invalid credentials");

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    @Override
    /*the support method is where we decide the authentication method that we intend to use
     *you can have multiple provider information  */
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
