package br.com.loginService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "applications")
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 200, nullable = false)
    private String name;

    // TODO: implementar autenticacao direto com o front do client futuramente
    @Column(name = "allowed_origin", length = 200)
    private String allowedOrigin;

    @Column(name = "template_html", nullable = false, columnDefinition = "TEXT")
    private String html;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    @Column(name = "owner_email", nullable = false, unique = true, length = 254)
    private String ownerEmail;

    @Column(nullable = false)
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Application(String name, String html, String ownerEmail, boolean enabled) {
        this.name = name;
        this.html = html;
        this.ownerEmail = ownerEmail;
        this.enabled = enabled;
    }
}
