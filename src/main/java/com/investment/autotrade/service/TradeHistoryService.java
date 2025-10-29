package com.investment.autotrade.service;

import com.investment.autotrade.entity.TradeHistory;
import com.investment.autotrade.repo.TradeHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeHistoryService {
    private final TradeHistoryRepo tradeHistoryRepo;

    public TradeHistory save(TradeHistory entity) {
        return tradeHistoryRepo.save(entity);
    }
}
