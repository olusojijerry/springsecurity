package com.jerryssec.springsecuritysetup.config;

import com.jerryssec.springsecuritysetup.exceptionHandling.CustomAccessDeniedHandler;
import com.jerryssec.springsecuritysetup.exceptionHandling.CustomBasicAuthenticationEntryPoint;
import com.jerryssec.springsecuritysetup.filter.CsrfCookieFilter;
import com.jerryssec.springsecuritysetup.handler.CustomAuthenticationFailureHandler;
import com.jerryssec.springsecuritysetup.handler.CustomAuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityNonProdConfig {
    @Autowired
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();
        /*Handling CORS error on spring security*/
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();

            corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
            corsConfiguration.setMaxAge(3600L);
            return corsConfiguration;
        }));
//      this allow all api calls to the application
//        http.authorizeHttpRequests((req) -> req.anyRequest().permitAll());
//      this disallow all request to the application
//        http.authorizeHttpRequests((req) -> req.anyRequest().denyAll());
//        this configuration is used to allow http connections.
//        the config below is to setup session timeout redirect page and the total number of session a particular user the
//        maxSessionsPreventsLogin will prevent the user from setting up another session without logging out of the previous session.
        /*tells spring security not to store the JSESSIONID inside the security context holder let spring security take care of it*/
        http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
                .sessionManagement(smc -> smc
                /*Always create session for me (JSESSIONID)*/
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .sessionFixation(sfc -> sfc.changeSessionId())
                        .invalidSessionUrl("/invalidSession")
                        .maximumSessions(1).maxSessionsPreventsLogin(true))
                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure())
                /*Configuring CSRF Token into the cookie implementation*/
                 .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestHandler)
                         /*use this to ignore csrf on apis that are for public use*/
                         .ignoringRequestMatchers("")
                         .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http
                .authorizeHttpRequests((req) -> req.
                requestMatchers( "/register", "/login/**", "/assets/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/dashboard", "/welcome").authenticated());
//        http.authorizeHttpRequests((req) -> req.anyRequest()
        /*this is to specify apis to be authenticated */
//                .requestMatchers("").authenticated()
        /*this is to specify apis that can go without credential*/

        /*this is to specify apis */
//                .requestMatchers("").denyAll());
//        http.formLogin(Customizer.withDefaults());
//        setting up the login page designed
        http.formLogin(flc -> flc.loginPage("/login").usernameParameter("userid").passwordParameter("secretPwd").defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler));
        http.logout(loc -> loc.logoutSuccessUrl("/login?logout=true").invalidateHttpSession(true)
                .clearAuthentication(true).deleteCookies("JSESSIONID"));

        //changing the username and password parameteres on the webpage
//        http.formLogin(flc -> flc.loginPage("/login").usernameParameter("usrid").passwordParameter("secretPwd"));
        /*this is to disable form login*/
//        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable() );
//        here you can setup the entrypoint for authentication
        http.httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));
//        another way to setup authentication entry point and to set it globally is below
//        http.exceptionHandling(ehc -> ehc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); //when this is used then a global entry point is set
//        setting access denied handler has to be done globally
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));

        return http.build();
    }

//    @Bean
////    Using database connection to the database.
//    public UserDetailsService userDetailsService(DataSource dataSource){
//        return new JdbcUserDetailsManager(dataSource);
//    }

//    @Bean
//    /*this method is for in memory storage */
//    public UserDetailsService userDetailsService(){
//        /*the noop allows the user to be authenticated without encoding the password i.e the password will be saved without hashing*/
//        /*the prefix before the password defines the algorithm that is to be used to encrypt the password */
//        UserDetails user = User.withUsername("user").password("{noop}12345").authorities("read").build();
//        UserDetails admin = User.withUsername("admin").password("54321").authorities("admin").build();
////        User.withUsername("user").password("12345").authorities("read").build();
//        return  new InMemoryUserDetailsManager( user, admin);
//    }

    @Bean
    public PasswordEncoder passwordEncoder(){
//        this is used to define a specific algorithm to be used for
//        return new BCryptPasswordEncoder();
//        this allows you to specify the algorithm you will like to use using prefix in the password.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    /*this method is to compromised passwords or the complexity of the password*/
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
