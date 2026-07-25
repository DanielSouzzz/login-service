package br.com.loginService.repository;

import br.com.loginService.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findSessionByRefreshToken(String refreshToken);
}