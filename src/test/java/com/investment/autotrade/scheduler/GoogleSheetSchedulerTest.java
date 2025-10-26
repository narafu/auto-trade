package com.investment.autotrade.scheduler;

import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.dto.response.kis.OverseasOrder;
import com.investment.autotrade.service.GoogleSheetService;
import com.investment.autotrade.service.KisTradingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoogleSheetSchedulerTest {

    @Autowired
    GoogleSheetService sheetService;
    @Autowired
    KisTradingService kisTradingService;

    @Test
    void executeScheduledTasks() {
        var readRange = "Privacy!A1:C9";
        var data = sheetService.getPrivacyExcel(readRange);

        for (OrderInfo buyOrder : data.getBuyOrders()) {
            OverseasOrder result = kisTradingService.sendOverseasOrder(buyOrder, true);
            System.out.println("result = " + result);
        }

        for (OrderInfo sellOrder : data.getSellOrders()) {
            OverseasOrder result = kisTradingService.sendOverseasOrder(sellOrder, false);
            System.out.println("result = " + result);
        }
    }
}