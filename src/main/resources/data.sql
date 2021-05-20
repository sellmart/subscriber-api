DROP TABLE IF EXISTS subscriptions;

CREATE TABLE subscriptions (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  card_number VARCHAR(30) NOT NULL,
  card_network VARCHAR(100) NOT NULL,
  country_code VARCHAR(5) DEFAULT NULL,
  expiration_year VARCHAR(2),
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);