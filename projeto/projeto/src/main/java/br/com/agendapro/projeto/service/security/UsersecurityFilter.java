package br.com.agendapro.projeto.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class UsersecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/users/login")) { // se o endpoint for de login, não exige hearders
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            // logs para verificar o valor do cabeçalho Authorization
            System.out.println("Authorization Header: " + authHeader);

            UsernamePasswordAuthenticationToken auth = UserTokenUtil.validate(request);

            // logs para verificar o token de autenticação
            System.out.println("Authenticated Token: " + auth);

            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("Authentication Token is null");
            }
        } else {
            System.out.println("No Authorization Header");
        }

        filterChain.doFilter(request, response);
    }
}