-- First, create a temporary column with the new enum values
ALTER TABLE orders ADD COLUMN payment_status_new ENUM(
    'SUCCEEDED',
    'PENDING',
    'FAILED',
    'PAID',
    'UNPAID',
    'NO_PAYMENT_REQUIRED',
    'OPEN',
    'COMPLETE',
    'EXPIRED',
    'UNKNOWN',
    'REFUNDED'
) DEFAULT NULL;

-- Map old values to new values
UPDATE orders SET payment_status_new = 'PENDING' WHERE payment_status = 'CREATED';
UPDATE orders SET payment_status_new = 'PENDING' WHERE payment_status = 'PROCESSING';
UPDATE orders SET payment_status_new = 'PENDING' WHERE payment_status = 'REQUIRES_ACTION';
UPDATE orders SET payment_status_new = 'FAILED' WHERE payment_status = 'CANCELED';
UPDATE orders SET payment_status_new = 'FAILED' WHERE payment_status = 'ERROR';
UPDATE orders SET payment_status_new = 'SUCCEEDED' WHERE payment_status = 'SUCCEEDED';
UPDATE orders SET payment_status_new = 'PENDING' WHERE payment_status = 'PENDING';
UPDATE orders SET payment_status_new = 'FAILED' WHERE payment_status = 'FAILED';
UPDATE orders SET payment_status_new = 'REFUNDED' WHERE payment_status = 'REFUNDED';

-- Drop the old column
ALTER TABLE orders DROP COLUMN payment_status;

-- Rename the new column to the original name
ALTER TABLE orders CHANGE COLUMN payment_status_new payment_status ENUM(
    'SUCCEEDED',
    'PENDING',
    'FAILED',
    'PAID',
    'UNPAID',
    'NO_PAYMENT_REQUIRED',
    'OPEN',
    'COMPLETE',
    'EXPIRED',
    'UNKNOWN',
    'REFUNDED'
) DEFAULT NULL;