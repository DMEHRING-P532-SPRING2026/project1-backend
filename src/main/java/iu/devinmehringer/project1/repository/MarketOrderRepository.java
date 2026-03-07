package iu.devinmehringer.project1.repository;

import iu.devinmehringer.project1.model.trade.MarketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketOrderRepository extends JpaRepository<MarketOrder, Long> {
}
