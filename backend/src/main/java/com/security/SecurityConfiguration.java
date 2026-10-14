package com.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtFilter;
    private final List<String> allowedOrigins;

    public SecurityConfiguration(JwtAuthenticationFilter jwtFilter,
                                 @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}") String origins) {
        this.jwtFilter = jwtFilter;
        this.allowedOrigins = Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                        .accessDeniedHandler(forbiddenHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/login", "/api/auth/register", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/faculty/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/students").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assignments/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/assignments/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/assignments/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/notices/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/notices/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/notices/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/events/*/registrations").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/*/registrations/me").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/events/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/events/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole("FACULTY", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/clubs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/clubs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clubs/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/students/**").denyAll()
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/complaints/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Location"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication is required\"}");
        };
    }

    private AccessDeniedHandler forbiddenHandler() {
        return (request, response, exception) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource\"}");
        };
    }
}
