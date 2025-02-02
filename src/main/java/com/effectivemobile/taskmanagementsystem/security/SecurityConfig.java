package com.effectivemobile.taskmanagementsystem.security;

import com.effectivemobile.taskmanagementsystem.security.filter.JwtAuthenticationFilter;
import com.effectivemobile.taskmanagementsystem.util.AppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        secureEndpoints(http);

        return http
                //разрешение запросов со всех доменов
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .csrf(AbstractHttpConfigurer::disable) //возможность работать с проксированными HTTP-запросам
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void secureEndpoints(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.POST, "/api/v1/sign-in", "api/v1/sign-up").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/swagger-ui/**", "api/v1/api-docs/**").permitAll()

                        //task
                        .requestMatchers(HttpMethod.GET, "api/v1/task/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/v1/task").authenticated()
                        .requestMatchers(HttpMethod.PUT, "api/v1/task/status").authenticated()
                        .requestMatchers(HttpMethod.PUT, "api/v1/task").hasAuthority(AppRole.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "api/v1/task").hasAuthority(AppRole.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "api/v1/task/{id}")
                                .hasAuthority(AppRole.ROLE_ADMIN.name())

                        //comment
                        .requestMatchers(HttpMethod.POST, "api/v1/comment").authenticated()
                        .requestMatchers(HttpMethod.PUT, "api/v1/comment").authenticated()
                        .requestMatchers("api/v1/comment/**").hasAuthority(AppRole.ROLE_ADMIN.name())

                        .anyRequest().authenticated()
                );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
