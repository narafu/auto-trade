package com.investment.autotrade.repo;

import com.investment.autotrade.entity.TradeHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeHistoryRepo extends CrudRepository<TradeHistory, Long> {
}
