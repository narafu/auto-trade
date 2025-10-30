package com.investment.autotrade.dto.response.excel;

import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.enums.OrderType;
import com.investment.autotrade.enums.TradeType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Builder
public class Privacy {
    private LocalDate orderDate;
    List<OrderInfo> orders;

    public static Privacy from(List<List<Object>> dataXy) {
        List<Object> firstX = dataXy.getFirst();
        if (!isValid(firstX.get(1), firstX.get(3))) {
            return null;
        }

        var ticker = dataXy.get(3).getFirst().toString();

        List<OrderInfo> orders = new ArrayList<>();
        for (int i = 4; i < 10; i++) {
            OrderInfo orderInfo = setOrderFromExcel(ticker, dataXy.get(i));
            if (orderInfo != null) {
                orders.add(orderInfo);
            }
        }

        return Privacy.builder()
                .orderDate(LocalDate.parse(firstX.getFirst().toString()))
                .orders(orders)
                .build();
    }

    private static OrderInfo setOrderFromExcel(String ticker, List<Object> list) {
        try {
            var tradeType = TradeType.fromCode(list.get(1).toString());
            var price = list.get(2).toString().replace("$", "");
            var quantity = list.get(3).toString();

            return OrderInfo.builder()
                    .ticker(ticker)
                    .tradeType(tradeType)
                    .price(Float.parseFloat(price))
                    .quantity(getQuantity(quantity))
                    .orderType(OrderType.LIMIT)
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

    private static boolean isValid(Object B1, Object D1) {
        try {
            if ("false".equalsIgnoreCase(B1.toString())) {
                log.info("No data...");
                return false;
            }

            if ( "true".equalsIgnoreCase(D1.toString())) {
                log.info("Already executed...");
                return false;
            }

            return "true".equalsIgnoreCase(B1.toString())
                    && "false".equalsIgnoreCase(D1.toString());

        } catch (Exception e) {
            return false;
        }
    }
}
