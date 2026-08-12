package br.com.loginService.repository;

import br.com.loginService.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsApplicationByOwnerEmail(@Param("email") String email);

    Optional<Application> findApplicationByApiKey(String apiKey);
}
