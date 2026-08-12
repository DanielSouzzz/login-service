package br.com.loginService.service.auth;

import br.com.loginService.exception.ApplicationException;
import br.com.loginService.exception.ErrorEnum;
import br.com.loginService.model.Session;
import br.com.loginService.model.User;
import br.com.loginService.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class SessionService {
    private static final long EXPIRATION = 30L * 24 * 60 * 60;

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String create(User user, String ip) {

        String refreshToken = generateToken();

        Session session = new Session(
                user,
                DigestUtils.sha256Hex(refreshToken),
                Instant.now().plusSeconds(EXPIRATION),
                ip
        );

        sessionRepository.save(session);

        return refreshToken;
    }

    public Session validate(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);

        Session session = sessionRepository.findSessionByRefreshToken(tokenHash)
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        if (session.getRevokedAt() != null) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        if (session.getExpiresAt().isBefore(Instant.now())) {
            throw new ApplicationException(ErrorEnum.INVALID_CREDENTIALS);
        }

        return session;
    }

    @Transactional
    public String refreshToken(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApplicationException(ErrorEnum.INVALID_CREDENTIALS));

        String refreshToken = generateToken();

        session.setRefreshToken(DigestUtils.sha256Hex(refreshToken));

        return refreshToken;
    }

    private String generateToken() {
        byte[] bytes = new byte[256];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);
    }

    public void revokeSessions(long userId) {
        sessionRepository.revokeAllSessions(userId);
    }
}
