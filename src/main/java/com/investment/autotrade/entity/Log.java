package com.investment.autotrade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.logging.LogLevel;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SuperBuilder
@Getter
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private LogLevel type;

    @Column
    private String content;

    @Column
    private String systemMessage;

    @Column
    private String userMessage;
}
