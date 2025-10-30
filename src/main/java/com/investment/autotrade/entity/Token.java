package com.investment.autotrade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Token {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false)
    private LocalDateTime expireddAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}
