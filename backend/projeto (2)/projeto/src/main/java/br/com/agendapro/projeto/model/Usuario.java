package br.com.agendapro.projeto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class Usuario {
    private int id;
    private String fullName;
    private String email;
    private String password;

}
