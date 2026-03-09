package iu.devinmehringer.project1.model.StockHolding;

import iu.devinmehringer.project1.model.stock.Stock;
import iu.devinmehringer.project1.model.user.User;
import jakarta.persistence.*;

@Entity
@Table(name="stock_holdings")
public class StockHolding {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="stock_id")
    private Stock stock;

    private Integer quantity;

    public StockHolding() {}

    public StockHolding(User user, Stock stock, Integer quantity) {
        this.user = user;
        this.stock = stock;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
