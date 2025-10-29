package com.investment.autotrade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 매수/매도 구분
 */
@Getter
@RequiredArgsConstructor
public enum TradeType {

    BUY("매수"),
    SELL("매도");

    private final String code;

    public static TradeType fromCode(String code) {
        for (TradeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
