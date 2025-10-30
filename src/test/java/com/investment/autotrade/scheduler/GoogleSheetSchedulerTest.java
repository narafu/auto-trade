package com.investment.autotrade.scheduler;

import com.investment.autotrade.dto.response.kis.OverseasOrder;
import com.investment.autotrade.service.GoogleSheetService;
import com.investment.autotrade.service.KisTradingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class GoogleSheetSchedulerTest {

    @Autowired
    GoogleSheetService sheetService;
    @Autowired
    KisTradingService kisTradingService;


    @Test
    void executeScheduledTasks() {
        System.out.println("Reading data from Google Sheets...");
        var data = sheetService.getPrivacyExcel("Privacy!A1:D10");

        if (data == null) {
            return;
        }

        data.getOrders().forEach(order -> {
            OverseasOrder result = kisTradingService.sendOverseasOrder(order);

            System.out.println("result = " + result);
        });

        sheetService.updateData("Privacy!D1", true);

        System.out.println("Finished scheduled tasks.");
    }
}