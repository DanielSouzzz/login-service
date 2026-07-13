package br.com.loginService.repository;

import br.com.loginService.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findFirstByUserIdAndUsedFalseOrderByCreatedAtDesc(Long id);
}