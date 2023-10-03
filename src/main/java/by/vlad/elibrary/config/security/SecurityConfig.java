package by.vlad.elibrary.config.security;

import by.vlad.elibrary.model.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfigurationSource configurationSource;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(configurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> { auth
                        .requestMatchers(HttpMethod.PATCH, "/order/accept/**", "/order/reject/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/order/", "/order/reserve/**", "/order/return/**").hasAuthority(Role.CLIENT.name())
                        .requestMatchers(HttpMethod.DELETE, "/order/","/order/**").hasAuthority(Role.CLIENT.name())
                        .requestMatchers(HttpMethod.POST, "/order/").hasAuthority(Role.CLIENT.name())
                        .requestMatchers(HttpMethod.GET, "/order/").hasAnyAuthority(Role.CLIENT.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/book/**","/book", "/author","/author/**", "/genre", "/genre/**", "/publisher", "/publisher/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/book/**","/book", "/author","/author/**", "/genre", "/genre/**", "/publisher", "/publisher/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/book/**","/book", "/author","/author/**", "/genre", "/genre/**", "/publisher", "/publisher/**").permitAll()
                        .requestMatchers("/client/register/**", "/client/login/**").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/system/**").permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint);
                    exception.accessDeniedHandler(accessDeniedHandler);
                });

        return httpSecurity.build();
    }
}
