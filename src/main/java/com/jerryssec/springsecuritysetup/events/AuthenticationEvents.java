package com.jerryssec.springsecuritysetup.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {
/*Here you can define the various events that you want to happen and the expected output*/
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent accessEvent){
        log.info("login successful for the user {}", accessEvent.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent accessEvent){
        log.info("login failed for the user {} due to {}", accessEvent.getAuthentication().getName(), accessEvent.getException().getMessage());
    }
}
