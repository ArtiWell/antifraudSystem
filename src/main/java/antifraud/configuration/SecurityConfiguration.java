package antifraud.configuration;

import antifraud.enums.UserRolesEnum;
import antifraud.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(
            AuthenticationProvider authenticationProvider,
            HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)                           // For modifying requests via Postman
                .exceptionHandling(handing -> handing
                        .authenticationEntryPoint(getRestAuthenticationEntryPoint()) // Handles auth error
                )
                .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                .authorizeHttpRequests(requests -> requests// manage access
                        .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(UserRolesEnum.ADMINISTRATOR.toString(),
                                UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole(UserRolesEnum.ADMINISTRATOR.toString())
                        .requestMatchers(HttpMethod.PUT, "/api/auth/access").hasRole(UserRolesEnum.ADMINISTRATOR.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasRole(UserRolesEnum.ADMINISTRATOR.toString())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasRole(UserRolesEnum.MERCHANT.toString())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/**").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/**").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasRole(UserRolesEnum.SUPPORT.toString())
                        .requestMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasRole(UserRolesEnum.SUPPORT.toString())

                        .requestMatchers("/actuator/shutdown").permitAll()// needs to run test

                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                ).authenticationProvider(authenticationProvider)
                // other configurations
                .build();
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint getRestAuthenticationEntryPoint() {
        return (request, response, authException)
                -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }

    @Bean
    public AuthenticationProvider getAuthenticationProvider(UserService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getEncoder());
        provider.setUserDetailsService(userService);
        return provider;
    }
}
