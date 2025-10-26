package com.investment.autotrade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 해외거래소 코드 열거형
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum ExchangeCode {
    /**
     * 나스닥 (NASDAQ)
     */
    NASD("NASD", "나스닥"),

    /**
     * 뉴욕증권거래소 (NYSE)
     */
    NYSE("NYSE", "뉴욕증권거래소"),

    /**
     * 아멕스 (AMEX)
     */
    AMEX("AMEX", "아멕스"),

    /**
     * 홍콩거래소 (SEHK)
     */
    SEHK("SEHK", "홍콩거래소"),

    /**
     * 중국상해거래소 (SHAA)
     */
    SHAA("SHAA", "중국상해거래소"),

    /**
     * 중국심천거래소 (SZAA)
     */
    SZAA("SZAA", "중국심천거래소"),

    /**
     * 일본거래소 (TKSE)
     */
    TKSE("TKSE", "일본거래소"),

    /**
     * 베트남 하노이거래소 (HASE)
     */
    HASE("HASE", "베트남 하노이거래소"),

    /**
     * 베트남 호치민거래소 (VNSE)
     */
    VNSE("VNSE", "베트남 호치민거래소");

    private final String code;
    private final String description;

    /**
     * 코드로 ExchangeCode를 찾습니다.
     *
     * @param code 거래소 코드
     * @return 해당하는 ExchangeCode, 없으면 null
     */
    public static ExchangeCode fromCode(String code) {
        for (ExchangeCode exchangeCode : values()) {
            if (exchangeCode.code.equals(code)) {
                return exchangeCode;
            }
        }
        return null;
    }

    /**
     * 매수 주문에 사용할 TR ID를 반환합니다.
     *
     * @return 매수 주문 TR ID
     */
    public String getBuyTrId() {
        return switch (this) {
            case NASD, NYSE, AMEX -> "TTTT1002U"; // 미국 매수
            case SEHK -> "TTTS1002U"; // 홍콩 매수
            case SHAA -> "TTTS0202U"; // 중국상해 매수
            case SZAA -> "TTTS0305U"; // 중국심천 매수
            case TKSE -> "TTTS0308U"; // 일본 매수
            case HASE, VNSE -> "TTTS0311U"; // 베트남 매수
        };
    }

    /**
     * 매도 주문에 사용할 TR ID를 반환합니다.
     *
     * @return 매도 주문 TR ID
     */
    public String getSellTrId() {
        return switch (this) {
            case NASD, NYSE, AMEX -> "TTTT1006U"; // 미국 매도
            case SEHK -> "TTTS1001U"; // 홍콩 매도
            case SHAA -> "TTTS1005U"; // 중국상해 매도
            case SZAA -> "TTTS0304U"; // 중국심천 매도
            case TKSE -> "TTTS0307U"; // 일본 매도
            case HASE, VNSE -> "TTTS0310U"; // 베트남 매도
        };
    }

    /**
     * 매수/매도 여부에 따라 적절한 TR ID를 반환합니다.
     *
     * @param isBuy true면 매수, false면 매도
     * @return 해당하는 TR ID
     */
    public String getTrId(boolean isBuy) {
        return isBuy ? getBuyTrId() : getSellTrId();
    }

}
