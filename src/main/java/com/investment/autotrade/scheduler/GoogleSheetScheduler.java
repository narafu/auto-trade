package com.investment.autotrade.scheduler;

import com.investment.autotrade.dto.OrderInfo;
import com.investment.autotrade.dto.response.kis.OverseasOrder;
import com.investment.autotrade.service.GoogleSheetService;
import com.investment.autotrade.service.KisTradingService;
import com.investment.autotrade.service.LogService;
import com.investment.autotrade.service.TradeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleSheetScheduler {

    private static final ZoneId NEW_YORK_ZONE = ZoneId.of("America/New_York");
    private final GoogleSheetService sheetService;
    private final KisTradingService kisTradingService;
    private final LogService logService;
    private final TradeHistoryService tradeHistoryService;

    /**
     * 미국 주식 시장 마감 30분 전부터 10분 간격으로 실행되는 스케줄러.
     * ZonedDateTime을 사용하여 서머타임(DST)을 자동으로 처리합니다.
     */
    @Scheduled(cron = "${app.scheduler.cron.pre-close}", zone = "${app.scheduler.timezone}")
    public void checkAndExecutePreCloseTasks() {
        ZonedDateTime nowInNewYork = ZonedDateTime.now(NEW_YORK_ZONE);

        // 미국 주식 시장은 주말에 열리지 않음
        if (isWeekend(nowInNewYork)) {
            return;
        }

        int hour = nowInNewYork.getHour();
        int minute = nowInNewYork.getMinute();

        // 미국 동부 시간 기준 오후 3시 30분, 40분, 50분인지 확인 (장 마감 30분 전부터)
        boolean isPreCloseTime = hour == 15 && (minute == 30 || minute == 40 || minute == 50);

        if (isPreCloseTime) {
            log.info("Executing pre-close tasks. Current New York time: {}", nowInNewYork);
            executeScheduledTasks();
        } else {
            log.debug("Skipping pre-close tasks. Current New York time: {}", nowInNewYork);
        }
    }

    /**
     * 실제 스케줄링 작업을 수행하는 메서드.
     */
    private void executeScheduledTasks() {
        log.info("Reading data from Google Sheets...");
        var data = sheetService.getPrivacyExcel("Privacy!A1:D10");

        if (data == null) {
            return;
        }

        for (OrderInfo order : data.getOrders()) {
            OverseasOrder result = kisTradingService.sendOverseasOrder(order);

            log.info("result = {}", result);
        }

        sheetService.updateData( "Privacy!D1", true);

        log.info("Finished scheduled tasks.");
    }

    /**
     * 뉴욕 시간 기준으로 주말(토요일 또는 일요일)인지 확인합니다.
     */
    private boolean isWeekend(ZonedDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
