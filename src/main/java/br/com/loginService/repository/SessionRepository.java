package br.com.loginService.repository;

import br.com.loginService.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findSessionByRefreshToken(String refreshToken);

    @Modifying
    @Query(value = """
        UPDATE sessions
        SET revoked_at = NOW()
        WHERE user_id = :id
        """, nativeQuery = true)
    void revokeAllSessions(@Param("id") long id);
}