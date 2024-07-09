SQL: 

CREATE TABLE accounts (
  id int NOT NULL AUTO_INCREMENT,
  username varchar(255) DEFAULT NULL,
  balance double DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE stock_price_history (
  id int NOT NULL AUTO_INCREMENT,
  closingprice double DEFAULT NULL,
  date datetime(6) DEFAULT NULL,
  ticker varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `stocks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `stock_ticker` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `stock_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `stock_ticker` (`stock_ticker`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
