package com.nexusmarket.domain;

import com.nexusmarket.domain.enums.UserRole;
import com.nexusmarket.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime registrationDate = LocalDateTime.now();

    protected User(String fullName, String email, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    public void changeStatus(UserStatus newStatus) {
        this.status = newStatus;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }
}
