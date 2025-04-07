ALTER TABLE orders
ADD COLUMN checkout_session_expires_at DATETIME(6) DEFAULT NULL;