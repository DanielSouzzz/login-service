package br.com.loginService.repository;

import br.com.loginService.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users u where u.email = :email and u.status = 'ACTIVE'", nativeQuery = true)
    Optional<User> findUserByEmailAndActiveStatus(@Param("email") String email);

    @Query(value = "select * from users u where u.id = :id and u.status = 'ACTIVE'", nativeQuery = true)
    Optional<User> findUserByIdAndActiveStatus(@Param("id") Long id);

    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);
}
