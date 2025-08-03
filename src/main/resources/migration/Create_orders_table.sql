-- Create_orders_table.sql
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      distance INT NOT NULL,
                                      status VARCHAR(20) NOT NULL
    );