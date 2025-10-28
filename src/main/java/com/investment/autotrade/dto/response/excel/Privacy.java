package com.investment.autotrade.dto.response.excel;

import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.enums.OrderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Privacy {
    private LocalDate orderDate;
    List<OrderInfo> buyOrders;
    List<OrderInfo> sellOrders;

    public static Privacy from(List<List<Object>> dataXy) {
        List<Object> firstX = dataXy.getFirst();
        if (!isValid(firstX.getLast())) {
            return null;
        }

        var ticker = firstX.get(1).toString();

        List<OrderInfo> buyOrders = new ArrayList<>();
        for (int i = 3; i < 6; i++) { // TODO 매수 갯수가 3개가 아닐 경우 index 오류
            buyOrders.add(setOrderFromExcel(ticker, dataXy.get(i)));
        }

        List<OrderInfo> sellOrders = new ArrayList<>();
        for (int i = 6; i < 9; i++) { // TODO 매도 갯수가 3개가 아닐 경우 index 오류
            sellOrders.add(setOrderFromExcel(ticker, dataXy.get(i)));
        }

        return Privacy.builder()
                .orderDate(LocalDate.parse(firstX.getFirst().toString()))
                .buyOrders(buyOrders)
                .sellOrders(sellOrders)
                .build();
    }

    private static OrderInfo setOrderFromExcel(String ticker, List<Object> list) {
        try {
            var price = list.get(1).toString();
            var quantity = list.get(2).toString();

            return OrderInfo.builder()
                    .ticker(ticker)
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

    private static boolean isValid(Object value) {
        try {
            return "true".equalsIgnoreCase(value.toString());
        } catch (Exception e) {
            return false;
        }
    }
}
