package br.com.agendapro.projeto.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/public/**").permitAll()  // Permitir acesso público a esses endpoints
                .anyRequest().authenticated() // Requer autenticação para qualquer outro endpoint
        ).formLogin(formLogin -> formLogin.loginPage("/login")  // Página de login customizada
                .permitAll()  // Permitir acesso público à página de login
        ).logout(logout -> logout.permitAll()  // Permitir logout público
        );
        return http.build();
    }
}