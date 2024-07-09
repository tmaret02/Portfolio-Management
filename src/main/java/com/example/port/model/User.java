package com.example.port.model;

import javax.persistence.*;

@Entity
@Table(name = "user_info")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "initial_balance")
    private Double initialBalance;

    @Column(name = "remaining_balance")
    private Double remainingBalance;

    public User() {}

    public User(Long userId, String username, Double initialBalance, Double remainingBalance) {
        this.userId = userId;
        this.username = username;
        this.initialBalance = initialBalance;
        this.remainingBalance = remainingBalance;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}