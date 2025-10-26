package com.investment.autotrade.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 해외주식 주문구분 열거형
 */
@Getter
@ToString
@RequiredArgsConstructor
public enum OrderType {
    /**
     * 지정가 주문
     */
    LIMIT("00", "지정가"),

    /**
     * MOO (Market On Open) - 장개시시장가
     */
    MOO("31", "장개시시장가"),

    /**
     * LOO (Limit On Open) - 장개시지정가
     */
    LOO("32", "장개시지정가"),

    /**
     * MOC (Market On Close) - 장마감시장가
     */
    MOC("33", "장마감시장가"),

    /**
     * LOC (Limit On Close) - 장마감지정가
     */
    LOC("34", "장마감지정가");

    private final String code;
    private final String description;

    /**
     * 코드로 OrderType을 찾습니다.
     *
     * @param code 주문구분 코드
     * @return 해당하는 OrderType, 없으면 null
     */
    public OrderType fromCode(String code) {
        for (OrderType orderType : values()) {
            if (orderType.code.equals(code)) {
                return orderType;
            }
        }
        return null;
    }
}
