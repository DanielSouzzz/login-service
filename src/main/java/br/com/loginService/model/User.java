package br.com.loginService.model;

import br.com.loginService.model.enums.StatusUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "name is mandatory")
    @Size(min = 3, message = "the name must have at least 3 characters")
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Email(message = "invalid email")
    @NotBlank(message = "email is mandatory")
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "phone is mandatory")
    @Column(name = "phone", length = 15, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusUser status = StatusUser.PENDING;
}
