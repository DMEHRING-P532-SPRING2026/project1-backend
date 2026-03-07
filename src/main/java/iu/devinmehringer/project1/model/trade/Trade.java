package iu.devinmehringer.project1.model.trade;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    private String ticker;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private Side side;
    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private BigDecimal price;
    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime executedAt;

    protected Trade() {}

    public Trade(OrderType orderType, String ticker, Integer quantity, Side side, TradeStatus status) {
        this.orderType = orderType;
        this.ticker = ticker;
        this.quantity = quantity;
        this.side = side;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
}
