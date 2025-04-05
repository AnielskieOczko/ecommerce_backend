-- V7__updates_order_table_for_checkout.sql
ALTER TABLE orders
ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'PLN';

ALTER TABLE orders
CHANGE COLUMN payment_intent_id checkout_session_url VARCHAR(255) NULL;