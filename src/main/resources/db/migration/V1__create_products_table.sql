CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    category VARCHAR(100),
    image_url VARCHAR(255),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Add check constraint for price (MySQL 8.0.16+ supports CHECK constraints)
ALTER TABLE products ADD CONSTRAINT chk_price CHECK (price > 0);

-- Add unique constraint on name
ALTER TABLE products ADD CONSTRAINT uk_products_name UNIQUE (name);

-- Add indexes for common queries
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_available ON products(available);
