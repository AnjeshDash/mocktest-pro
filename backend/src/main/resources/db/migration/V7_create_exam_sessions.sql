-- V7__create_exam_sessions.sql
CREATE TABLE exam_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_id UUID NOT NULL UNIQUE REFERENCES purchases(id),
    attendee_id UUID NOT NULL REFERENCES users(id),
    mock_test_id UUID NOT NULL REFERENCES mock_tests(id),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    current_section_index INTEGER NOT NULL DEFAULT 0,
    current_question_index INTEGER NOT NULL DEFAULT 0,
    section_start_time TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    submitted_at TIMESTAMP WITH TIME ZONE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    tab_switch_count INTEGER NOT NULL DEFAULT 0,
    is_flagged BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_exam_sessions_attendee ON exam_sessions(attendee_id, status);
CREATE INDEX idx_exam_sessions_test ON exam_sessions(mock_test_id, status);