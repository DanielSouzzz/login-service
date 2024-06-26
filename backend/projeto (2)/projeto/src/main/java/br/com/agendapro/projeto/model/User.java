package br.com.agendapro.projeto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId") // Especifica o nome exato da coluna no banco de dados
    private Integer id;

    @Column(name = "username", length = 200, nullable = true)
    private String username;

    @Column(name = "email", length = 200, nullable = true)
    private String email;

    @Column(name = "password", columnDefinition = "TEXT", nullable = true)
    private String password;

    @Column(name = "phone", length = 15, nullable = true)
    private String phone;

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
