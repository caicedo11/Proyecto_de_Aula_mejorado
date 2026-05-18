package com.Educational_harmonie.Educational_harmonie.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.Educational_harmonie.Educational_harmonie.model.Usuario;
import com.Educational_harmonie.Educational_harmonie.service.Usuarioservice;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(Usuarioservice usuarioservice) {
        return username -> {

            Usuario usuario = usuarioservice.buscarPorUsuario(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

            // VALIDAR SI EL USUARIO ESTÁ BLOQUEADO
            if (usuario.getEstado() != null &&
                usuario.getEstado().equalsIgnoreCase("INACTIVO")) {

                throw new UsernameNotFoundException("Usuario bloqueado");
            }

            int idCargo = usuario.getIdCargo() != null ? usuario.getIdCargo() : 5;

            return User.builder()
                    .username(usuario.getUsuario())
                    .password(usuario.getContrasena())
                    .roles(obtenerRolPorCargo(idCargo))
                    .build();
        };
    }

    // ROLES CORREGIDOS SEGÚN LA BASE DE DATOS REAL
    private String obtenerRolPorCargo(int idCargo) {
        switch (idCargo) {
            case 1:
                return "ADMIN";

            case 2:
                return "ACUDIENTE"; // ✅ Corregido: ID 2 es Acudiente en la BD

            case 3:
                return "DOCENTE";   // ✅ Corregido: ID 3 es Docente en la BD

            case 4:
                return "ESTUDIANTE";

            default:
                return "USUARIO";
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // API
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            );

        return http.build();
    }

    // WEB
    @Bean
    @Order(2)
    public SecurityFilterChain appSecurity(
            HttpSecurity http,
            DaoAuthenticationProvider provider) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .authenticationProvider(provider)

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                        "/",
                        "/home",
                        "/login",
                        "/registro/**",
                        "/registro/completar",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/static/**"
                ).permitAll()

                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/docente/**").hasRole("DOCENTE")
                .requestMatchers("/acudiente/**").hasRole("ACUDIENTE")
                .requestMatchers("/estudiante/**").hasRole("ESTUDIANTE")

                .anyRequest().authenticated()
            )

            .formLogin(form -> form

                .loginPage("/login")
                .loginProcessingUrl("/login")

                .usernameParameter("username")
                .passwordParameter("password")

                .defaultSuccessUrl("/redirect", true)

                .failureUrl("/login?error=true")

                .permitAll()
            )

            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl("/login?logout")

                .permitAll()
            );

        return http.build();
    }
}