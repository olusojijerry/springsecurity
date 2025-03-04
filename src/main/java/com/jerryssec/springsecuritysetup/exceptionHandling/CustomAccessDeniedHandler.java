package com.jerryssec.springsecuritysetup.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String message = (accessDeniedException != null && accessDeniedException.getMessage() != null) ? accessDeniedException.getMessage() : "Unauthorization Failed";
        String path = request.getRequestURI();
//        this is a key value pair to be added to the header you can also accept expected headers values from you request here.
        response.setHeader("error-reason", "Unauthorization Failed");
//      the config below enforce the default failure response
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.setStatus(HttpStatus.FORBIDDEN.value());

//        Changing the response on 401 error failure
        String jsonResponse = String.format("{\"timestamp\":\"%s\",\"status\":%d, \"error\":\"%s\" \"message\":\"%s\", \"path\":\"%s\" }",
                currentDateTime, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), message, path);

        response.getWriter().write(jsonResponse);
    }
}
