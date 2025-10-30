package com.investment.autotrade.service;

import com.investment.autotrade.dto.response.excel.Privacy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class GoogleSheetServiceTest {

    @Autowired
    GoogleSheetService sheetService;

    @Test
    void getPrivacyExcel() {
        var readRange = "Privacy!A1:D10";
        Privacy privacyExcel = sheetService.getPrivacyExcel(readRange);
        System.out.println("privacyExcel = " + privacyExcel);
    }

    @Test
    void readData() {
        var readRange = "Privacy!A1:D10";
        List<List<Object>> result = sheetService.readData(readRange);
        System.out.println("result = " + result);
    }

    @Test
    void updateData() {
        var readRange = "Privacy!D1";
        List<List<Object>> newValue = List.of(List.of(true));
        sheetService.updateData(readRange, newValue);
    }

}