package com.jerryssec.springsecuritysetup.exceptionHandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

/*this is where you define the implementation for exception handling for the Authentication exceptions i.e exception with error code 401
* You can set requirements for the the application like headers values */
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage() : "Unauthorised";
        String path = request.getRequestURI();
//        this is a key value pair to be added to the header you can also accept expected headers values from you request here.
        response.setHeader("error-reason", "Authentication Failed");
//      the config below enforce the default failure response
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

//        Changing the response on 401 error failure
        String jsonResponse = String.format("{\"timestamp\":\"%s\",\"status\":%d, \"error\":\"%s\" \"message\":\"%s\", \"path\":\"%s\" }",
                currentDateTime, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), message, path);

        response.getWriter().write(jsonResponse);
    }
}
