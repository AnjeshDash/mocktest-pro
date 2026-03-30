-- V5__create_purchases.sql
CREATE TABLE purchases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attendee_id UUID NOT NULL REFERENCES users(id),
    mock_test_id UUID NOT NULL REFERENCES mock_tests(id),
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) DEFAULT 'SIMULATED',
    transaction_id VARCHAR(255) UNIQUE,
    qr_code_data TEXT,
    is_qr_used BOOLEAN NOT NULL DEFAULT FALSE,
    purchased_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(attendee_id, mock_test_id)
);

CREATE INDEX idx_purchases_attendee ON purchases(attendee_id, payment_status);
CREATE INDEX idx_purchases_test ON purchases(mock_test_id, payment_status);