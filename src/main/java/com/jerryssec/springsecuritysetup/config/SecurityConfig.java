package com.jerryssec.springsecuritysetup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
//      this allow all api calls to the application
//        http.authorizeHttpRequests((req) -> req.anyRequest().permitAll());
//      this disallow all request to the application
//        http.authorizeHttpRequests((req) -> req.anyRequest().denyAll());
        http.authorizeHttpRequests((req) -> req.requestMatchers( "/register").permitAll()
                .anyRequest().authenticated());
//        http.authorizeHttpRequests((req) -> req.anyRequest()
                /*this is to specify apis to be authenticated */
//                .requestMatchers("").authenticated()
                /*this is to specify apis that can go without credential*/

                /*this is to specify apis */
//                .requestMatchers("").denyAll());
        http.formLogin(Customizer.withDefaults());
        /*this is to disable form login*/
//        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable() );
        http.httpBasic(Customizer.withDefaults());
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
