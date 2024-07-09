package com.example.port.model;

import javax.persistence.*;

@Entity
@Table(name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "stock_id")
    private String stockId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "last_price")
    private Double lastPrice;

    public Portfolio() {}

    public Portfolio(Long portfolioId, Long userId, String stockId, Integer quantity, Double lastPrice) {
        this.portfolioId = portfolioId;
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.lastPrice = lastPrice;
    }

    // Getters and setters
    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }
}
