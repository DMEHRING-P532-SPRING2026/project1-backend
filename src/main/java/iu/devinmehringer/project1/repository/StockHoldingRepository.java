package iu.devinmehringer.project1.repository;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.StockHolding.StockHolding;
import iu.devinmehringer.project1.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding, Long> {
    Optional<StockHolding> findByUserAndStock(User user, Stock stock);
    List<StockHolding> findByUser(User user);
}
