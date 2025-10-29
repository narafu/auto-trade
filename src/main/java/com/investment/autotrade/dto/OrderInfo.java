package com.investment.autotrade.dto;

import com.investment.autotrade.enums.OrderType;
import com.investment.autotrade.enums.TradeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderInfo {
    private String ticker;
    private float price;
    private int quantity; // 0일 경우 전부
    private OrderType orderType;
    private TradeType tradeType;
}
