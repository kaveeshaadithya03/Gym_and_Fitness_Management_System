-- Update Payment and Invoice Tables to Match Models
-- Run this SQL script to update your database schema

-- Drop existing tables if they exist (to recreate with correct schema)
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS payments;

-- Create payments table with correct schema
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    booking_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    method VARCHAR(50),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PAID',
    FOREIGN KEY (member_id) REFERENCES member_profiles(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL
);

-- Create invoices table with correct schema matching Invoice model
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    payment_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2),
    description TEXT,
    status VARCHAR(50) DEFAULT 'ISSUED',
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member_profiles(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_payments_member ON payments(member_id);
CREATE INDEX idx_payments_booking ON payments(booking_id);
CREATE INDEX idx_payments_date ON payments(date_time);
CREATE INDEX idx_invoices_payment ON invoices(payment_id);
CREATE INDEX idx_invoices_member ON invoices(member_id);
CREATE INDEX idx_invoices_number ON invoices(invoice_number);

-- Insert sample payment and invoice for testing (optional)
-- Uncomment the lines below if you want to create test data
-- Make sure member_id 1 exists in your member_profiles table

/*
INSERT INTO payments (member_id, amount, payment_type, method, date_time, status)
VALUES (1, 50.00, 'SESSION', 'CARD', NOW(), 'PAID');

-- Get the last inserted payment ID and create invoice
SET @payment_id = LAST_INSERT_ID();

INSERT INTO invoices (invoice_number, payment_id, member_id, issued_date, total_amount, tax_amount, description, status)
VALUES (
    CONCAT('INV-', UNIX_TIMESTAMP(NOW())),
    @payment_id,
    1,
    NOW(),
    50.00,
    5.00,
    'Training Session Payment',
    'PAID'
);
*/

-- Verify tables were created
SELECT 'Payments table created successfully' AS status;
SELECT 'Invoices table created successfully' AS status;
