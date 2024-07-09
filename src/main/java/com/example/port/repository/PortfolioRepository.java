package com.example.port.repository;

import com.example.port.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Portfolio p WHERE p.userId = ?1")
    void deleteByUserId(Long userId);
}