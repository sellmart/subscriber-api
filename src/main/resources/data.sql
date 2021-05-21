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

DROP TABLE IF EXISTS credit_card_number_formats;

CREATE TABLE credit_card_number_format_rules (
    id INT AUTO_INCREMENT  PRIMARY KEY,
    card_network VARCHAR(100) NOT NULL,
    iin_range VARCHAR(255) NOT NULL,
    length_range VARCHAR(50) NOT NULL
);

INSERT INTO credit_card_number_format_rules
(card_network,iin_range,length_range)
VALUES
('AMEX','[34,37]','[15]'),
('MASTERCARD','[51,52,53,54,55,222100-272099]','[16]'),
('VISA','[4]','[13-19]'),
('DISCOVER','[6011,622126-622925,644,645,646,647,648,649,65]','[16-19]');
