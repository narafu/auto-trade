package com.investment.autotrade.entity;

import com.investment.autotrade.enums.ExchangeCode;
import com.investment.autotrade.enums.OrderType;
import com.investment.autotrade.enums.TradeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SuperBuilder
public class TradeHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExchangeCode exchangeCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private int quantity;

    @Column
    private LocalDateTime orderTime;
}
