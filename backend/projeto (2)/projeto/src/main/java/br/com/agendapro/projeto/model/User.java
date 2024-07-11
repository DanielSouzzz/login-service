package br.com.agendapro.projeto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId") // Especifica o nome exato da coluna no banco de dados
    private Integer id;

    @NotBlank(message = "name is mandatory")
    @Size(min = 3, message = "the name must have at least 3 characters")
    @Column(name = "username", length = 200, nullable = false)
    private String username;

    @Email(message = "invalid email")
    @NotBlank(message = "email is mandatory")
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password", columnDefinition = "TEXT", nullable = false)
    private String password;

    @NotBlank(message = "phone is mandatory")
    @Column(name = "phone", length = 15, nullable = false)
    private String phone;
}
