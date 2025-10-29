package com.investment.autotrade.service;

import com.investment.autotrade.entity.Log;
import com.investment.autotrade.repo.LogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepo logRepo;

    public Log save(Log entity) {
        return logRepo.save(entity);
    }
}
