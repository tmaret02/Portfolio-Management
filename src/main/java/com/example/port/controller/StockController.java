package com.example.port.controller;

import com.example.port.model.Portfolio;
import com.example.port.model.User;
import com.example.port.repository.PortfolioRepository;
import com.example.port.repository.UserRepository;
import com.example.port.service.TwelveDataService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private static final Logger logger = Logger.getLogger(StockController.class.getName());

    private final TwelveDataService twelveDataService;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public StockController(TwelveDataService twelveDataService,
                           UserRepository userRepository,
                           PortfolioRepository portfolioRepository) {
        this.twelveDataService = twelveDataService;
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> newUser) {
        String username = (String) newUser.get("username");
        Double initialBalance = null;
        
        try {
            Number balance = (Number) newUser.get("initialBalance");
            if (balance != null) {
                initialBalance = balance.doubleValue();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Initial balance is required.");
            }
        } catch (ClassCastException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid initial balance format.");
        }
    
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username.");
        }
    
        try {
            User user = new User();
            user.setUsername(username);
            user.setInitialBalance(initialBalance);
            user.setRemainingBalance(initialBalance);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        }
    }
    
    

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{username}/portfolio")
    public ResponseEntity<String> addToPortfolio(
            @PathVariable String username,
            @RequestParam String stockId,
            @RequestParam Integer quantity) {

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            // Fetch the latest price for the stock using the TwelveDataService
            Double lastPrice = twelveDataService.fetchLatestPrice(stockId);
            if (lastPrice == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found: " + stockId);
            }

            Portfolio newPortfolioEntry = new Portfolio(null, user.getUserId(), stockId, quantity, lastPrice);
            portfolioRepository.save(newPortfolioEntry);

            return ResponseEntity.status(HttpStatus.CREATED).body("Added to portfolio successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding to portfolio: " + e.getMessage());
        }
    }

    @GetMapping("/{username}/portfolio")
    public ResponseEntity<List<Portfolio>> getPortfolioByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        List<Portfolio> portfolio = portfolioRepository.findByUserId(user.getUserId());
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/{username}/portfolio")
    public ResponseEntity<String> clearPortfolio(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        portfolioRepository.deleteByUserId(user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body("Portfolio cleared successfully");
    }

    @GetMapping("/{username}/portfolio/historical")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getHistoricalData(
            @PathVariable String username,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        List<Portfolio> portfolio = portfolioRepository.findByUserId(user.getUserId());
        Map<String, List<Map<String, String>>> historicalData = new HashMap<>();

        for (Portfolio entry : portfolio) {
            List<Map<String, String>> data = twelveDataService.fetchHistoricalData(entry.getStockId(), startDate, endDate);
            // Multiply each close price by the quantity and store the updated data
            for (Map<String, String> dataPoint : data) {
                double closePrice = Double.parseDouble(dataPoint.get("close"));
                dataPoint.put("close", String.valueOf(closePrice * entry.getQuantity()));
            }
            historicalData.put(entry.getStockId(), data);
        }

        return ResponseEntity.ok(historicalData);
    }

    @GetMapping("/{username}/balance")
    public ResponseEntity<Map<String, Double>> getBalance(@PathVariable String username) {
        logger.info("Fetching balance for user: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        logger.info("User found: " + user.getUsername());

        List<Portfolio> portfolio = portfolioRepository.findByUserId(user.getUserId());
        double portfolioValue = portfolio.stream()
                .mapToDouble(entry -> entry.getQuantity() * entry.getLastPrice())
                .sum();

        logger.info("Calculated portfolio value: " + portfolioValue);

        double remainingBalance = user.getInitialBalance() - portfolioValue;
        user.setRemainingBalance(remainingBalance);
        userRepository.save(user); // Update remaining balance in the database

        Map<String, Double> balance = new HashMap<>();
        balance.put("initialBalance", user.getInitialBalance());
        balance.put("remainingBalance", user.getRemainingBalance());

        return ResponseEntity.ok(balance);
    }
}