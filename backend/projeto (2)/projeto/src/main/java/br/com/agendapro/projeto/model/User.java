package br.com.agendapro.projeto.model;

import jakarta.persistence.*;
import lombok.Data;

@Data

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId") // Especifica o nome exato da coluna no banco de dados
    private Integer id;

    @Column(name = "username", length = 200, nullable = false)
    private String username;

    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Column(name = "password", columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(name = "phone", length = 15, nullable = false)
    private String phone;
}
