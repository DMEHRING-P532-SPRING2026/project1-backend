package iu.devinmehringer.project1.repository;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatus(TradeStatus status);
}
