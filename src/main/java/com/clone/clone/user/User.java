package com.clone.clone.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;


@Getter
@Entity
@Setter
@Table(name = "users")
@NoArgsConstructor
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "marketing", nullable = false)
    private boolean marketing;


    public User(String username, String password, String email, boolean marketing) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.marketing=marketing;
    }
}
