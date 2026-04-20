package com.vehiclerental.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false)
    private String name;
    @Email @NotBlank @Column(unique = true, nullable = false)
    private String email;
    @NotBlank @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;
    private String phone;
    private String address;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_active")
    private boolean active = true;
    @PrePersist public void prePersist() { createdAt = LocalDateTime.now(); }
    public enum Role { CUSTOMER, ADMIN }
}
