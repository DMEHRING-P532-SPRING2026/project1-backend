package iu.devinmehringer.project1.repository;

import iu.devinmehringer.project1.model.trade.Trade;
import iu.devinmehringer.project1.model.trade.TradeStatus;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatus(TradeStatus status);
    List<Trade> findByStatusIn(List<TradeStatus> statuses);
    List<Trade> findByStatusAndUser(TradeStatus status, User user);
    List<Trade> findByStatusInAndUser(List<TradeStatus> statuses, User user);
}
