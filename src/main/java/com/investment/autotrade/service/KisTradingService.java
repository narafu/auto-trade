package com.investment.autotrade.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.investment.autotrade.common.Const;
import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.dto.response.kis.OverseasBalance;
import com.investment.autotrade.dto.response.kis.OverseasOrder;
import com.investment.autotrade.dto.response.kis.OverseasPsamount;
import com.investment.autotrade.enums.ExchangeCode;
import com.investment.autotrade.enums.OrderType;
import com.investment.autotrade.enums.TradeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 한국투자증권 API를 사용하여 계좌 잔고 조회 및 주문(LOC)을 수행하는 서비스.
 * KisTokenService를 통해 토큰을 주입받아 사용합니다.
 */
@Slf4j
@Service
public class KisTradingService {

    @Value("${kis.api.account-number}")
    private String accountNumber;

    @Value("${kis.account-product-code}")
    private String accountProductCode;

    private final KisApiClient kisApiClient;

    public KisTradingService(KisApiClient kisApiClient) {
        this.kisApiClient = kisApiClient;
    }

    // =======================================================
    // 2. 해외 주식 계좌 잔고 조회 (inquireOverseasAccountBalance)
    // =======================================================

    /**
     * 해외 주식 계좌 잔고 정보를 조회합니다.
     * KIS API는 잔고 조회 시 GET 요청을 사용하며, 필요한 파라미터는 쿼리 파라미터로 전달합니다.
     */
    public OverseasBalance inquireOverseasAccountBalance() {
        String trId = "TTTS3012R"; // 해외 주식 잔고/평가 조회 TR ID (실전투자용)

        // KIS API는 특정 조회 TR ID에 대해 쿼리 파라미터를 요구합니다.
        // OVRS_EXCG_CD: 해외거래소코드 (전체:'*')
        // TR_CRCY_CD: 거래통화코드 (USD, HKD, CNY, JPY, VND)
        // CTX_AREA_FK200, CTX_AREA_NK200: 연속조회 파라미터 (최초 조회시 공란)
        String uri = Const.INQUIRE_OVERSEAS_BALANCE_URI + "?CANO=" + accountNumber +
                "&ACNT_PRDT_CD=" + accountProductCode +
                "&OVRS_EXCG_CD=*" +
                "&TR_CRCY_CD=USD" +
                "&CTX_AREA_FK200=" +
                "&CTX_AREA_NK200=";

        log.debug("Account Number: {}, Product Code: {}", accountNumber, accountProductCode);
        JsonNode result = kisApiClient.performGetRequest(uri, trId, "Overseas Account Balance Inquiry");

        return OverseasBalance.from(result);
    }

    // =======================================================
    // 3. 해외 주식 매수가능금액 조회 (inquireOverseasPsamount)
    // =======================================================

    /**
     * 해외 주식 매수가능금액을 조회합니다.
     *
     * @param exchangeCode 해외 거래소 코드 (예: "NASD" - 나스닥)
     * @param stockCode    종목 코드 (예: "TSLA")
     * @param orderPrice   주문 단가 (예: "23.8")
     * @return 매수가능금액 조회 응답 JSON
     */
    public OverseasPsamount inquireOverseasPsamount(String exchangeCode, String stockCode, String orderPrice) {
        String trId = "TTTS3007R"; // 해외 주식 매수가능금액 조회 TR ID (실전투자용)

        // KIS API는 특정 조회 TR ID에 대해 쿼리 파라미터를 요구합니다.
        String uri = Const.INQUIRE_PSAMOUNT_URI + "?CANO=" + accountNumber +
                "&ACNT_PRDT_CD=" + accountProductCode +
                "&OVRS_EXCG_CD=" + exchangeCode +
                "&OVRS_ORD_UNPR=" + orderPrice +
                "&ITEM_CD=" + stockCode;

        log.debug("Order Price: {}", orderPrice);
        var result = kisApiClient.performGetRequest(uri, trId, "Overseas Purchase Amount Inquiry");

        return OverseasPsamount.from(result);
    }

    // =======================================================
    // 4. 해외 주식 일반주문 (sendOverseasOrder)
    // =======================================================

    /**
     * 해외 주식 일반주문을 실행합니다. (LOC, MOO, MOC 등 다양한 주문구분 지원)
     *
     * @param exchangeCode 해외 거래소 코드 (ExchangeCode enum)
     * @param stockCode    종목 코드 (예: "TSLA")
     * @param quantity     주문 수량
     * @param price        주문 가격 (지정가: 가격 명시, 시장가: "0")
     * @param isBuy        true면 매수, false면 매도
     * @param orderType    주문구분 (OrderType enum)
     * @return 주문 응답 JSON
     */
    public OverseasOrder sendOverseasOrder(ExchangeCode exchangeCode, String stockCode, int quantity, String price,
                                           TradeType tradeType, OrderType orderType) {
        boolean isBuy = tradeType.equals(TradeType.BUY);
        String trId = exchangeCode.getTrId(isBuy);

        // 요청 본문 생성 (JSON 형식)
        String requestBody = String.format("""
                        {
                            "CANO": "%s",
                            "ACNT_PRDT_CD": "%s",
                            "OVRS_EXCG_CD": "%s",
                            "PDNO": "%s",
                            "ORD_QTY": "%d",
                            "OVRS_ORD_UNPR": "%s",
                            "CTAC_TLNO": "",
                            "MGCO_APTM_ODNO": "",
                            "SLL_TYPE": "%s",
                            "ORD_SVR_DVSN_CD": "0",
                            "ORD_DVSN": "%s"
                        }
                        """, accountNumber, accountProductCode, exchangeCode.getCode(), stockCode, quantity, price,
                isBuy ? "" : "00", orderType.getCode());

        String operation = String.format("Overseas %s Order for %s (%s, Qty: %d, Price: %s, OrderType: %s)",
                tradeType.getCode(), stockCode, exchangeCode, quantity, price, orderType);

        var result = kisApiClient.performPostRequest(Const.OVERSEAS_ORDER_URI, trId, requestBody, operation);
        return OverseasOrder.from(result);
    }

    public OverseasOrder sendOverseasOrder(OrderInfo orderInfo) {
        return sendOverseasOrder(ExchangeCode.AMEX,
                orderInfo.getTicker(),
                orderInfo.getQuantity(),
                String.valueOf(orderInfo.getPrice()),
                orderInfo.getTradeType(),
                orderInfo.getOrderType());
    }
}