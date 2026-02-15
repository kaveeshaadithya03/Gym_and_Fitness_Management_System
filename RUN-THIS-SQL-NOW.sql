-- ============================================
-- RUN THIS SQL TO ENABLE PAYMENT INTEGRATION
-- ============================================
-- Database: gym_management
-- Purpose: Create/Update payment and invoice tables
-- ============================================

USE gym_management;

-- Drop existing tables (backup first if you have important data!)
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS payments;

-- Create payments table
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

-- Create invoices table
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

-- Verify tables were created
SELECT 'Payments table created successfully' AS status;
DESCRIBE payments;

SELECT 'Invoices table created successfully' AS status;
DESCRIBE invoices;

-- Optional: Set rates for trainers and doctors if not already set
-- Uncomment and modify these lines as needed:

/*
-- Set hourly rate for all trainers (default $75/hour)
UPDATE trainer_profiles SET hourly_rate = 75.00 WHERE hourly_rate IS NULL;

-- Set consultation fee for all doctors (default $100)
UPDATE doctor_profiles SET consultation_fee = 100.00 WHERE consultation_fee IS NULL;
*/

-- Done! Now restart your Spring Boot application
SELECT 'Database update complete! Restart your application.' AS message;
