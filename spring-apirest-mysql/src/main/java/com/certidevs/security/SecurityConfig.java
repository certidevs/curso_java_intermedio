package com.certidevs.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@AllArgsConstructor
@Configuration
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {
    private final RequestJWTFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Para versiones >= 6.1 de Spring Security
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/users/register").permitAll()
                        .requestMatchers("files/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "customers").hasAnyAuthority("ROLE_ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "customers/**").hasAnyAuthority("ROLE_ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "customers/**").hasAnyAuthority("ROLE_ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "customers/**").hasAnyAuthority("ROLE_ADMIN")
                        // ALTERNATIVA:
                                .requestMatchers(HttpMethod.POST, "customers").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "customers/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "customers/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "customers/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                ) .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //    /**
//     * Versiones < 6.1
//     * Personalizar el objeto HttpSecurity de Spring Security para utilizar filtro JWT y proteger controladores
//     * Proteger rutas: IMPORTANTE, no colocar una / delante de la ruta
//     * ❌ /users/login
//     * ✅ users/login
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // Sin estados, sin sesiones Http, ya que usamos tokens JWT
//        // La autenticación JWT es sin estado y no depende de sesiones o cookies
//        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        http.authorizeHttpRequests()
//                .requestMatchers("users/login").permitAll()
//                .requestMatchers("users/register").permitAll()
//                .requestMatchers("files/**").permitAll()
//                .requestMatchers("books").permitAll() // Permitimos ver libros por ser la página home
//                .requestMatchers(HttpMethod.POST, "books").hasAnyAuthority("ADMIN") // solo el ADMIN puede ver libros
//                .requestMatchers(HttpMethod.PUT, "books").hasAnyAuthority("ADMIN") // solo el ADMIN puede ver libros
//                .requestMatchers(HttpMethod.DELETE, "books").hasAnyAuthority("ADMIN") // solo el ADMIN puede ver libros
//                // .requestMatchers(HttpMethod.POST).hasAnyAuthority("ADMIN")
//                // .requestMatchers(HttpMethod.POST, "admin/**").hasAnyAuthority("ADMIN")
//                .anyRequest()
//                .authenticated();
//
//        // Asignar nuestro filtro personalizado de JWT
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
}