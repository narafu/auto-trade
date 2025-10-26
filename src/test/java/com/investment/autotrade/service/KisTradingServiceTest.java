package com.investment.autotrade.service;

import com.investment.autotrade.enums.ExchangeCode;
import com.investment.autotrade.enums.OrderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KisTradingServiceTest {

    @Autowired
    KisTradingService kisTradingService;

    @Test
    void testOverseasBalance() {
        // 해외 주식 계좌 잔고 조회
        var result = kisTradingService.inquireOverseasAccountBalance();
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasPsamount() {
        // 해외 주식 매수가능금액 조회
        var result = kisTradingService.inquireOverseasPsamount("AMEX", "SOXL", "50.0");
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasOrder() {
        // 해외 주식 지정가 주문
        var result = kisTradingService.sendOverseasOrder(ExchangeCode.AMEX, "SOXL", 1, "50.0", true, OrderType.LIMIT);
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasLocOrder() {
        // 해외 주식 LOC(장마감지정가) 매수 주문
        var result = kisTradingService.sendOverseasOrder(ExchangeCode.AMEX, "SOXL", 1, "50.0", true, OrderType.LOC);
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasMooOrder() {
        // 해외 주식 MOO(장개시시장가) 매수 주문
        var result = kisTradingService.sendOverseasOrder(ExchangeCode.AMEX, "SOXL", 1, "0", true, OrderType.MOO);
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasLooOrder() {
        // 해외 주식 LOO(장개시지정가) 매수 주문
        var result = kisTradingService.sendOverseasOrder(ExchangeCode.AMEX, "SOXL", 1, "50.0", true, OrderType.LOO);
        System.out.println("result = " + result);
    }

    @Test
    void testOverseasMocOrder() {
        // MOC(장마감시장가) 매수 주문
        var result = kisTradingService.sendOverseasOrder(ExchangeCode.AMEX, "SOXL", 1, "0", true, OrderType.MOC);
        System.out.println("result = " + result);
    }

}