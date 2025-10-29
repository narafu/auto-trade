package com.investment.autotrade.dto.response.excel;

import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.enums.OrderType;
import com.investment.autotrade.enums.TradeType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Privacy {
    private LocalDate orderDate;
    List<OrderInfo> orders;

    public static Privacy from(List<List<Object>> dataXy) {
        List<Object> firstX = dataXy.getFirst();
        if (!isValid(firstX.getLast())) {
            return null;
        }

        var ticker = firstX.get(1).toString();

        List<OrderInfo> orders = new ArrayList<>();
        for (int i = 4; i < 10; i++) {
            orders.add(setOrderFromExcel(ticker, dataXy.get(i)));
        }

        return Privacy.builder()
                .orderDate(LocalDate.parse(firstX.getFirst().toString()))
                .orders(orders)
                .build();
    }

    private static OrderInfo setOrderFromExcel(String ticker, List<Object> list) {
        try {
            var tradeType = TradeType.fromCode(list.get(1).toString());
            var price = list.get(2).toString();
            var quantity = list.get(3).toString();

            return OrderInfo.builder()
                    .ticker(ticker)
                    .price(Float.parseFloat(price))
                    .quantity(getQuantity(quantity))
                    .orderType(OrderType.LIMIT)
                    .tradeType(tradeType)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private static int getQuantity(String quantity) {
        if ("all".equalsIgnoreCase(quantity)) {
            return 0; // 전부
        }

        return Integer.parseInt(quantity);
    }

    private static boolean isValid(Object value) {
        try {
            return "true".equalsIgnoreCase(value.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
