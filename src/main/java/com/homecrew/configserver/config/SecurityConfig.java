package com.homecrew.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Locks down config-server now that it decrypts {@code {cipher}} values and serves the results.
 *
 * <p>
 * Lives in a {@code .config} sub-package on purpose: the shared {@code
 * config/spotbugs-exclude.xml} scopes its {@code THROWS_METHOD_THROWS_CLAUSE_BASIC_EXCEPTION}
 * exemption to {@code .config.} packages, because {@code HttpSecurity.build()} forces every
 * {@link SecurityFilterChain} bean to declare {@code throws Exception}.
 *
 * <p>
 * Before encryption this server only ever handed out {@code ${PLACEHOLDER}} strings, so leaving it
 * open cost nothing. It now returns real credentials, and {@code /decrypt} will turn any cipher
 * text back into plaintext for whoever asks, so both need a password in front of them.
 */
@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        // CSRF off, not overlooked. There is no browser session and no form here; the clients are
        // Spring Cloud Config clients sending HTTP basic. Leaving it on breaks POST /encrypt and
        // POST /decrypt with a 403 that reads like an authorisation problem.
        .csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> requests
            // Load-bearing exception. docker-compose.yml health-checks this server with
            // an unauthenticated `curl -fsS http://localhost:8888/actuator/health`. Behind
            // auth that is a 401, the container never reports healthy, and all ten clients
            // behind `depends_on: service_healthy` refuse to start - so the whole stack
            // fails to come up and the cause looks nothing like a security setting.
            //
            // Matched by path rather than with EndpointRequest so this does not depend on
            // Boot's actuator-security package, which has moved between versions.
            .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info")
            .permitAll()
            // Everything else: the config endpoints, /encrypt, /decrypt and /key.
            .anyRequest().authenticated())
        .httpBasic(basic -> {
        }).build();
  }
}
