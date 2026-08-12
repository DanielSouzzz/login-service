package br.com.loginService.repository;

import br.com.loginService.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users u where u.email = :email and u.status = 'ACTIVE' and u.application_id = :applicationId", nativeQuery = true)
    Optional<User> findUserByEmailAndActiveStatusAndApplicationId(@Param("email") String email, @Param( "applicationId") long applicationId);

    @Query(value = "select * from users u where u.id = :id and u.status = 'ACTIVE' and u.application_id = :applicationId", nativeQuery = true)
    Optional<User> findUserByIdAndActiveStatusAndApplicationId(@Param("id") Long id, @Param("applicationId") long applicationId);

    Optional<User> findUserByEmailAndApplicationId(String email, long ApplicationId);

    boolean existsUserByEmail(String email);
}
