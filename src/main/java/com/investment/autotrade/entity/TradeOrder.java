package com.investment.autotrade.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TradeOrder {
    private Long id;
    private LocalDateTime orderTime;
    private String ticker; // SOXL
    private String orderType; // 매수/매도
    private String orderDivision; // LOC(06), 지정가(01) 등
    private double price;
    private int quantity;
    private String apiOrderId; // 한국투자증권 주문번호
    private boolean isSuccess;
    private String apiResponse; // 주문 실패 시 응답 내용
}