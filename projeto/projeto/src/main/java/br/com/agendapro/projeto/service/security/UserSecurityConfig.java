package br.com.agendapro.projeto.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSec) throws Exception {
        httpSec
                .csrf(csrf -> csrf.disable()) // Desativa a proteção CSRF
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/users/login").permitAll() // permite acesso sem autenticação ao endpoint de login
                                .anyRequest().authenticated() // exige autenticação para qualquer outro endpoint
                )
                .addFilterBefore(new UsersecurityFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSec.build();
    }
}
