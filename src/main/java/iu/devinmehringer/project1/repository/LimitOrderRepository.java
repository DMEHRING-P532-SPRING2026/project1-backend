package iu.devinmehringer.project1.repository;

import iu.devinmehringer.project1.model.trade.LimitOrder;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitOrderRepository extends JpaRepository<LimitOrder, Long> {
    List<LimitOrder> findByStatus(TradeStatus status);
}
