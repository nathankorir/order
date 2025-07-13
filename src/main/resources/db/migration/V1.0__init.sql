CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE orders
(
    id             UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    order_number   VARCHAR(255) UNIQUE,
    customer_id    VARCHAR(255),
    status         VARCHAR(50),
    total_amount   NUMERIC(19, 2),
    order_date     TIMESTAMP,
    payment_status VARCHAR(50)        DEFAULT 'UNPAID',
    voided         BOOLEAN   NOT NULL DEFAULT false,
    created_at     TIMESTAMP NOT NULL DEFAULT Now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT Now()
);

CREATE TABLE order_item
(
    id           UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),
    order_id     UUID REFERENCES orders (id) ON DELETE CASCADE,
    product_id   BIGINT         NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    price        NUMERIC(19, 2) NOT NULL,
    quantity     INTEGER        NOT NULL,
    subtotal     NUMERIC(19, 2) NOT NULL,
    voided       BOOLEAN        NOT NULL DEFAULT false
);

-- Index searchable columns
-- CREATE INDEX idx_product_name ON product (name);