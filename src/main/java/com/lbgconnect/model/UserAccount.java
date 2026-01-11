package com.lbgconnect.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String location;
    private String avatarUrl;
    private String headline;

    @Column(length = 1000)
    private String bio;

    private Double rating;
    private Integer reviewsCount;
    private Boolean verified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_skills",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
